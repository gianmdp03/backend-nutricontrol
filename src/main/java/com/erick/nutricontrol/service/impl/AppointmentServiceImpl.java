package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol._enum.AppointmentStatus;
import com.erick.nutricontrol.dto.appointment.AppointmentDetailDTO;
import com.erick.nutricontrol.dto.appointment.AppointmentRequestDTO;
import com.erick.nutricontrol.exception.BadRequestException;
import com.erick.nutricontrol.exception.NotFoundException;
import com.erick.nutricontrol.mapper.AppointmentMapper;
import com.erick.nutricontrol.model.Appointment;
import com.erick.nutricontrol.model.ScheduleException;
import com.erick.nutricontrol.model.ScheduleRule;
import com.erick.nutricontrol.repository.AppointmentRepository;
import com.erick.nutricontrol.repository.ScheduleExceptionRepository;
import com.erick.nutricontrol.repository.ScheduleRuleRepository;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.security.user.repository.UserRepository;
import com.erick.nutricontrol.service.AppointmentService;
import com.erick.nutricontrol.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentServiceImpl implements AppointmentService {
  private final AppointmentRepository repository;
  private final AppointmentMapper mapper;
  private final ScheduleRuleRepository scheduleRuleRepository;
  private final ScheduleExceptionRepository scheduleExceptionRepository;
  private final UserRepository userRepository;
  private final EmailService emailService;

  @Value("${nutricontrol.appointments.days}")
  private Integer days;

  @Value("${nutricontrol.appointments.minutes-gap}")
  private Integer minutesGap;

  @Value("${nutricontrol.appointments.duration-minutes}")
  private Integer durationMinutes;

  @Override
  @Transactional
  public AppointmentDetailDTO addAppointment(String username, AppointmentRequestDTO dto) {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new NotFoundException("User not found"));
    User admin =
        userRepository
            .findById(dto.adminId())
            .orElseThrow(() -> new NotFoundException("Admin not found"));
    LocalTime calculatedEndTime = dto.startTime().plusMinutes(this.durationMinutes);

    Map<LocalDate, List<LocalTime>> availableSlots = getAvailableAppointments();
    List<LocalTime> slotsForRequestedDate =
        availableSlots.getOrDefault(dto.date(), Collections.emptyList());

    if (!slotsForRequestedDate.contains(dto.startTime())) {
      throw new BadRequestException(
          "El turno seleccionado ya no está disponible o está fuera de horario.");
    }
    Appointment appointment = mapper.toEntity(dto);
    appointment.setEndTime(calculatedEndTime);
    appointment.setAppointmentStatus(AppointmentStatus.PENDING);
    appointment.setUser(user);
    appointment.setAdmin(admin);

    emailService.sendEmail(
        user.getEmail(),
        "ASUNTO: COMPROBANTE DE TURNO",
        "COMPROBANTE DE TURNO PARA EL DÍA "
            + appointment.getDate()
            + "A LAS "
            + appointment.getStartTime());

    appointment = repository.save(appointment);

    return mapper.toDetailDTO(appointment);
  }

  // CONFIRMAR TURNO - EL METODO ANTERIOR RESERVA EL TURNO MIENTRAS EL USUARIO PAGA.
  // SI PAGA CIERTO TIEMPO Y EL USUARIO NO PAGA, SE ELIMINA EL TURNO.

  @Override
  public Map<LocalDate, List<LocalTime>> getAvailableAppointments() {
    List<ScheduleRule> scheduleRules = scheduleRuleRepository.findAll();
    List<ScheduleException> scheduleExceptions = scheduleExceptionRepository.findAll();

    LocalDate today = LocalDate.now();
    LocalDate endDate = today.plusDays(days);

    List<Appointment> bookedAppointments = repository.findByDateBetween(today, endDate);

    Map<LocalDate, Set<LocalTime>> bookedSlotsPerDay =
        bookedAppointments.stream()
            .collect(
                Collectors.groupingBy(
                    Appointment::getDate,
                    Collectors.mapping(Appointment::getStartTime, Collectors.toSet())));

    Map<LocalDate, List<LocalTime>> availability = new TreeMap<>();

    for (int i = 0; i < days; i++) {
      LocalDate currentDate = today.plusDays(i);
      DayOfWeek currentDayOfWeek = currentDate.getDayOfWeek();

      List<LocalTime> dailySlots = new ArrayList<>();

      // 1. PRIMERO: Buscamos la regla normal de trabajo para este día de la semana
      ScheduleRule ruleForDay =
          scheduleRules.stream()
              .filter(rule -> rule.getDayOfWeek().equals(currentDayOfWeek))
              .findFirst()
              .orElse(null);

      // Si trabaja este día, calculamos todos sus turnos
      if (ruleForDay != null) {
        dailySlots =
            calculateMinutes(ruleForDay.getStartTime(), ruleForDay.getEndTime(), minutesGap);

        // 2. SEGUNDO: Buscamos si hay una excepción (bloqueo/ausencia) para esta fecha exacta
        ScheduleException exceptionForDay =
            scheduleExceptions.stream()
                .filter(exception -> exception.getDate().equals(currentDate))
                .findFirst()
                .orElse(null);

        if (exceptionForDay != null) {
          // Si la hora de inicio y fin son iguales (ej: 00:00 a 00:00), asumimos que es día
          // libre/feriado
          if (exceptionForDay.getStartTime().equals(exceptionForDay.getEndTime())) {
            dailySlots.clear(); // Vaciamos todos los turnos del día
          } else {
            // Generamos los turnos que corresponden al bloqueo (ej: de 08:00 a 10:00)
            List<LocalTime> blockedSlots =
                calculateMinutes(
                    exceptionForDay.getStartTime(), exceptionForDay.getEndTime(), minutesGap);

            // Magia: Le restamos a los turnos normales, los turnos bloqueados
            dailySlots.removeAll(blockedSlots);
          }
        }
      }

      // 3. TERCERO: Limpiamos los que ya pasaron de hora hoy y los que ya están reservados
      if (!dailySlots.isEmpty()) {
        if (currentDate.equals(today)) {
          LocalTime now = LocalTime.now();
          dailySlots.removeIf(slot -> slot.isBefore(now));
        }

        Set<LocalTime> bookedToday =
            bookedSlotsPerDay.getOrDefault(currentDate, Collections.emptySet());
        dailySlots.removeAll(bookedToday);

        if (!dailySlots.isEmpty()) {
          availability.put(currentDate, dailySlots);
        }
      }
    }
    return availability;
  }

  private List<LocalTime> calculateMinutes(
      LocalTime startTime, LocalTime endTime, Integer minutesGap) {
    List<LocalTime> list = new ArrayList<>();
    LocalTime currentTime = startTime;

    while (!currentTime.plusMinutes(minutesGap).isAfter(endTime)) {
      list.add(currentTime);
      currentTime = currentTime.plusMinutes(minutesGap);
    }

    return list;
  }

  @Override
  public Page<AppointmentDetailDTO> listUserAppointments(String username, Pageable pageable) {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new NotFoundException("User not found"));
    Page<Appointment> page = repository.findByUser(user, pageable);
    if (page.isEmpty()) {
      return Page.empty();
    }
    return page.map(mapper::toDetailDTO);
  }

  @Override
  public Page<AppointmentDetailDTO> listAdminAppointments(String username, Pageable pageable) {
    User admin =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new NotFoundException("User not found"));
    Page<Appointment> page = repository.findByAdmin(admin, pageable);
    if (page.isEmpty()) {
      return Page.empty();
    }
    return page.map(mapper::toDetailDTO);
  }

  @Override
  @Transactional
  public void deleteAppointment(Long id) {
    Appointment appointment =
        repository.findById(id).orElseThrow(() -> new NotFoundException("Appointment not found"));
    repository.delete(appointment);
    // DEVOLUCION DE DINERO
  }

  @Override
  @Transactional
  public void adminDeleteAppointment(Long id, boolean refund) {
    Appointment appointment =
        repository.findById(id).orElseThrow(() -> new NotFoundException("Appointment not found"));
    repository.delete(appointment);
    // DEVOLUCION DE DINERO
  }
}
