package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol._enum.AppointmentStatus;
import com.erick.nutricontrol.dto.appointment.AppointmentDetailDTO;
import com.erick.nutricontrol.dto.appointment.AppointmentRequestDTO;
import com.erick.nutricontrol.dto.appointment.AvailableSlotDTO;
import com.erick.nutricontrol.dto.payment.PaymentOrderResponseDTO;
import com.erick.nutricontrol.dto.payment.PaymentRequestDTO;
import com.erick.nutricontrol.exception.BadRequestException;
import com.erick.nutricontrol.exception.ConflictException;
import com.erick.nutricontrol.exception.NotFoundException;
import com.erick.nutricontrol.mapper.AppointmentMapper;
import com.erick.nutricontrol.model.Appointment;
import com.erick.nutricontrol.model.Payment;
import com.erick.nutricontrol.model.ScheduleException;
import com.erick.nutricontrol.model.ScheduleRule;
import com.erick.nutricontrol.repository.AppointmentRepository;
import com.erick.nutricontrol.repository.PaymentRepository;
import com.erick.nutricontrol.repository.ScheduleExceptionRepository;
import com.erick.nutricontrol.repository.ScheduleRuleRepository;
import com.erick.nutricontrol.security.user.Enum.Role;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.security.user.repository.UserRepository;
import com.erick.nutricontrol.service.AppointmentService;
import com.erick.nutricontrol.service.EmailService;
import com.erick.nutricontrol.service.PaymentService;
import com.paypal.sdk.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.*;
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
  private final PaymentService paymentService;

  @Value("${nutricontrol.appointments.days}")
  private Integer days;

  @Value("${nutricontrol.appointments.minutes-gap}")
  private Integer minutesGap;

  @Value("${nutricontrol.appointments.duration-minutes}")
  private Integer durationMinutes;

  @Override
  @Transactional
  public PaymentOrderResponseDTO addAppointment(String username, AppointmentRequestDTO dto)
          throws IOException, ApiException {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("User not found"));
    User admin = userRepository.findById(dto.adminId())
            .orElseThrow(() -> new NotFoundException("Admin not found"));

    ZoneId doctorZone = ZoneId.of(admin.getTimezone() != null ? admin.getTimezone() : "America/Santo_Domingo");
    ZonedDateTime zdtStart = dto.startTime().atZoneSameInstant(doctorZone);

    LocalDate doctorDate = zdtStart.toLocalDate();
    LocalTime doctorStartTime = zdtStart.toLocalTime();
    LocalTime doctorEndTime = doctorStartTime.plusMinutes(this.durationMinutes);

    if (repository.existsByUserAndAdminAndDateAndStartTimeAndAppointmentStatusNot(user, admin, doctorDate, doctorStartTime, AppointmentStatus.CANCELLED)) {
      throw new ConflictException("Ya tenés una reserva en proceso para este profesional en este exacto horario. Por favor, aguardá un momento o revisá tu historial.");
    }

    LocalDate globalStart = LocalDate.now(ZoneOffset.UTC).minusDays(1);
    LocalDate globalEnd = LocalDate.now(ZoneOffset.UTC).plusDays(days + 2);

    List<ScheduleRule> adminRules = scheduleRuleRepository.findByAdminIn(List.of(admin));
    List<ScheduleException> adminExceptions = scheduleExceptionRepository.findByAdminIn(List.of(admin));
    List<Appointment> adminBooked = repository.findByAdminInAndDateBetween(List.of(admin), globalStart, globalEnd);

    Map<LocalDate, List<LocalTime>> availableSlots = calculateInMemoryAvailability(admin, adminRules, adminExceptions, adminBooked);

    List<LocalTime> slotsForRequestedDate = availableSlots.getOrDefault(doctorDate, Collections.emptyList());

    if (!slotsForRequestedDate.contains(doctorStartTime)) {
      throw new BadRequestException("El turno seleccionado ya no está disponible.");
    }

    Appointment appointment = mapper.toEntity(dto);
    appointment.setDate(doctorDate);
    appointment.setStartTime(doctorStartTime);
    appointment.setEndTime(doctorEndTime);
    appointment.setUser(user);
    appointment.setAdmin(admin);
    appointment.setAppointmentStatus(AppointmentStatus.PENDING);
    appointment.setStartTimeUtc(dto.startTime());
    appointment.setEndTimeUtc(dto.startTime().plusMinutes(this.durationMinutes));
    appointment = repository.save(appointment);

    return paymentService.createPaymentHold(new PaymentRequestDTO(appointment.getId()));
  }

  @Override
  public List<AvailableSlotDTO> getAvailableAppointments() {
    List<User> admins = userRepository.findByRole(Role.ROLE_ADMIN);
    if (admins.isEmpty()) return Collections.emptyList();

    LocalDate globalStart = LocalDate.now(ZoneOffset.UTC).minusDays(1);
    LocalDate globalEnd = LocalDate.now(ZoneOffset.UTC).plusDays(days + 2);

    List<ScheduleRule> allRules = scheduleRuleRepository.findByAdminIn(admins);
    List<ScheduleException> allExceptions = scheduleExceptionRepository.findByAdminIn(admins);
    List<Appointment> allBooked = repository.findByAdminInAndDateBetween(admins, globalStart, globalEnd);

    Map<Long, List<ScheduleRule>> rulesByAdmin = allRules.stream().collect(Collectors.groupingBy(r -> r.getAdmin().getId()));
    Map<Long, List<ScheduleException>> exceptionsByAdmin = allExceptions.stream().collect(Collectors.groupingBy(e -> e.getAdmin().getId()));
    Map<Long, List<Appointment>> bookedByAdmin = allBooked.stream().collect(Collectors.groupingBy(a -> a.getAdmin().getId()));

    List<AvailableSlotDTO> globalAvailableSlots = new ArrayList<>();

    for (User admin : admins) {
      Long adminId = admin.getId();
      List<ScheduleRule> adminRules = rulesByAdmin.getOrDefault(adminId, Collections.emptyList());
      List<ScheduleException> adminExceptions = exceptionsByAdmin.getOrDefault(adminId, Collections.emptyList());
      List<Appointment> adminBooked = bookedByAdmin.getOrDefault(adminId, Collections.emptyList());

      Map<LocalDate, List<LocalTime>> availability = calculateInMemoryAvailability(admin, adminRules, adminExceptions, adminBooked);
      ZoneId doctorZone = ZoneId.of(admin.getTimezone() != null ? admin.getTimezone() : "America/Santo_Domingo");

      for (Map.Entry<LocalDate, List<LocalTime>> entry : availability.entrySet()) {
        for (LocalTime time : entry.getValue()) {
          ZonedDateTime zdt = ZonedDateTime.of(entry.getKey(), time, doctorZone);
          OffsetDateTime utcTime = zdt.toOffsetDateTime().withOffsetSameInstant(ZoneOffset.UTC);

          globalAvailableSlots.add(new AvailableSlotDTO(
                  admin.getId(),
                  admin.getName() + " " + admin.getLastname(),
                  utcTime
          ));
        }
      }
    }

    globalAvailableSlots.sort(Comparator.comparing(AvailableSlotDTO::startTimeUTC));
    return globalAvailableSlots;
  }

  private Map<LocalDate, List<LocalTime>> calculateInMemoryAvailability(User admin, List<ScheduleRule> rules, List<ScheduleException> exceptions, List<Appointment> booked) {
    String tz = admin.getTimezone() != null ? admin.getTimezone() : "America/Santo_Domingo";
    ZoneId doctorZone = ZoneId.of(tz);
    LocalDate today = LocalDate.now(doctorZone);

    Map<LocalDate, Set<LocalTime>> occupiedSlotsPerDay = new HashMap<>();
    for (Appointment app : booked) {
      if (app.getAppointmentStatus() == AppointmentStatus.CANCELLED) {
        continue;
      }

      LocalDate d = app.getDate();
      occupiedSlotsPerDay.putIfAbsent(d, new HashSet<>());

      occupiedSlotsPerDay.get(d).add(app.getStartTime());
    }

    Map<LocalDate, List<LocalTime>> availability = new TreeMap<>();

    for (int i = 0; i < days; i++) {
      LocalDate currentDate = today.plusDays(i);
      List<LocalTime> dailySlots = new ArrayList<>();

      List<ScheduleRule> rulesForDay = rules.stream()
              .filter(r -> r.getDayOfWeek().equals(currentDate.getDayOfWeek()))
              .toList();

      for (ScheduleRule rule : rulesForDay) {
        dailySlots.addAll(calculateMinutes(rule.getStartTime(), rule.getEndTime(), minutesGap));
      }

      List<ScheduleException> exceptionsForDay = exceptions.stream()
              .filter(ex -> ex.getDate().equals(currentDate))
              .toList();

      for (ScheduleException ex : exceptionsForDay) {
        if (ex.getStartTime().equals(ex.getEndTime())) {
          dailySlots.clear();
          break;
        } else {
          dailySlots.removeAll(calculateMinutes(ex.getStartTime(), ex.getEndTime(), minutesGap));
        }
      }

      if (!dailySlots.isEmpty()) {
        if (currentDate.equals(today)) {
          LocalTime now = LocalTime.now(doctorZone);
          dailySlots.removeIf(slot -> slot.isBefore(now));
        }
        dailySlots.removeAll(occupiedSlotsPerDay.getOrDefault(currentDate, Collections.emptySet()));

        List<LocalTime> uniqueSlots = dailySlots.stream().distinct().sorted().toList();
        if (!uniqueSlots.isEmpty()) availability.put(currentDate, new ArrayList<>(uniqueSlots));
      }
    }
    return availability;
  }

  private List<LocalTime> calculateMinutes(LocalTime startTime, LocalTime endTime, Integer gap) {
    List<LocalTime> list = new ArrayList<>();
    LocalTime current = startTime;
    while (!current.plusMinutes(gap).isAfter(endTime)) {
      list.add(current);
      current = current.plusMinutes(gap);
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
    List<AppointmentStatus> statuses = new ArrayList<>();
    statuses.add(AppointmentStatus.CONFIRMED);
    statuses.add(AppointmentStatus.CANCELLED_REFUND);
    statuses.add(AppointmentStatus.CANCELLED_WITHOUT_REFUND);
    Page<Appointment> page = repository.findByAdminAndAppointmentStatusIn(admin, statuses, pageable);
    if (page.isEmpty()) {
      return Page.empty();
    }
    return page.map(mapper::toDetailDTO);
  }

  @Override
  @Transactional
  public void deleteAppointment(Long id, String username) {
    Appointment appointment =
        repository.findById(id).orElseThrow(() -> new NotFoundException("Appointment not found"));
    if (!appointment.getUser().getUsername().equals(username)) {
      throw new ConflictException("Acceso denegado: No puedes modificar un turno que pertenece a otro paciente.");
    }
    OffsetDateTime appointmentDateTime = appointment.getStartTimeUtc();
    boolean isMoreThan24HoursAhead = OffsetDateTime.now(ZoneOffset.UTC).plusHours(24).isBefore(appointmentDateTime);
    if (isMoreThan24HoursAhead) {
      processRefundIfApply(appointment, false);
    }
    else{
      forcePenaltyCapture(appointment);
    }
    repository.delete(appointment);
  }

  @Override
  @Transactional
  public void adminDeleteAppointment(Long id, boolean refund) {
    Appointment appointment =
        repository.findById(id).orElseThrow(() -> new NotFoundException("Appointment not found"));
    OffsetDateTime appointmentDateTime = appointment.getStartTimeUtc();
    boolean isMoreThan24HoursAhead = OffsetDateTime.now(ZoneOffset.UTC).plusHours(24).isBefore(appointmentDateTime);
    boolean finalRefundDecision = isMoreThan24HoursAhead || refund;

    processRefundIfApply(appointment, finalRefundDecision);

    repository.delete(appointment);
  }

  @Override
  public Page<AppointmentDetailDTO> getAllAppointments(Pageable pageable){
    Page<Appointment> page = repository.findAll(pageable);
    if(page.isEmpty()) {
      return Page.empty();
    }
    return page.map(mapper::toDetailDTO);
  }

  private void processRefundIfApply(Appointment appointment, boolean adminForcedRefund) {
    if (!appointment.getPayments().isEmpty()) {
      for (Payment payment : appointment.getPayments()) {
        try {
          String paymentStatus = payment.getStatus().name();
          if ("AUTHORIZED".equals(paymentStatus) && payment.getPaypalAuthorizationId() != null) {
            paymentService.voidPayment(payment.getPaypalAuthorizationId());
          } else if ("CAPTURED".equals(paymentStatus)
              && payment.getPaypalCaptureId() != null
              && adminForcedRefund) {
            paymentService.refundPayment(payment.getPaypalCaptureId());
          }
        } catch (Exception e) {
          throw new BadRequestException(
              "PayPal error in payment with id: " + payment.getId() + ": " + e.getMessage());
        }
      }
    }
  }

  private void forcePenaltyCapture(Appointment appointment) {
    if (!appointment.getPayments().isEmpty()) {
      for (Payment payment : appointment.getPayments()) {
        try {
          String paymentStatus = payment.getStatus().name();
          if ("AUTHORIZED".equals(paymentStatus) && payment.getPaypalAuthorizationId() != null) {
            paymentService.capturePayment(payment.getPaypalAuthorizationId());
          }
        } catch (Exception e) {
          throw new BadRequestException("PayPal error");
        }
      }
    }
  }
}
