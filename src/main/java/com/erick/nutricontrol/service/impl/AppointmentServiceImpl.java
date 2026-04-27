package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol._enum.AppointmentStatus;
import com.erick.nutricontrol.dto.appointment.AppointmentDetailDTO;
import com.erick.nutricontrol.dto.appointment.AppointmentListDTO;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
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

    @Override
    @Transactional
    public AppointmentDetailDTO addAppointment(String username, AppointmentRequestDTO dto) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        User admin = userRepository.findById(dto.adminId()).orElseThrow(() -> new NotFoundException("Admin not found"));
        boolean isTaken = repository.existsByDateAndStartTimeAndAppointmentStatusNot(
                dto.date(),
                dto.startTime(),
                AppointmentStatus.CANCELLED
        );

        if (isTaken) {
            throw new RuntimeException("This appointment is reserved");
        }

        boolean isException = scheduleExceptionRepository.existsByDate(dto.date());
        if (isException) {
            throw new BadRequestException("Invalid Date");
        }

        Appointment appointment = mapper.toEntity(dto);
        appointment.setAppointmentStatus(AppointmentStatus.PENDING);
        appointment.setUser(user);
        appointment.setAdmin(admin);
        appointment = repository.save(appointment);

        return mapper.toDetailDTO(appointment);
    }

    //CONFIRMAR TURNO - EL METODO ANTERIOR RESERVA EL TURNO MIENTRAS EL USUARIO PAGA.
    //SI PAGA CIERTO TIEMPO Y EL USUARIO NO PAGA, SE ELIMINA EL TURNO.

    @Override
    public Map<LocalDate, List<LocalTime>> getAvailableAppointments(Integer days, Integer minutesGap) {
        List<ScheduleRule> scheduleRules = scheduleRuleRepository.findAll();
        List<ScheduleException> scheduleExceptions = scheduleExceptionRepository.findAll();

        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);

        List<Appointment> bookedAppointments = repository.findByDateBetween(today, endDate);

        Map<LocalDate, Set<LocalTime>> bookedSlotsPerDay = bookedAppointments.stream()
                .collect(Collectors.groupingBy(
                        Appointment::getDate,
                        Collectors.mapping(Appointment::getStartTime, Collectors.toSet())
                ));

        Map<LocalDate, List<LocalTime>> availability = new TreeMap<>();

        for (int i = 0; i < days; i++) {
            LocalDate currentDate = today.plusDays(i);
            DayOfWeek currentDayOfWeek = currentDate.getDayOfWeek();

            boolean isException = scheduleExceptions.stream()
                    .anyMatch(exception -> exception.getDate().equals(currentDate));

            if (isException) {
                continue;
            }

            ScheduleRule ruleForDay = scheduleRules.stream()
                    .filter(rule -> rule.getDayOfWeek().equals(currentDayOfWeek))
                    .findFirst()
                    .orElse(null);

            if (ruleForDay != null) {
                List<LocalTime> dailySlots = calculateMinutes(ruleForDay, minutesGap);
                Set<LocalTime> bookedToday = bookedSlotsPerDay.getOrDefault(currentDate, Collections.emptySet());

                dailySlots.removeAll(bookedToday);

                if (!dailySlots.isEmpty()) {
                    availability.put(currentDate, dailySlots);
                }
            }
        }

        return availability;
    }

    private List<LocalTime> calculateMinutes(ScheduleRule aux, Integer minutesGap) {
        List<LocalTime> list = new ArrayList<>();
        LocalTime currentTime = aux.getStartTime();
        LocalTime endTime = aux.getEndTime();

        while (!currentTime.plusMinutes(minutesGap).isAfter(endTime)) {
            list.add(currentTime);
            currentTime = currentTime.plusMinutes(minutesGap);
        }

        return list;
    }

    @Override
    public Page<AppointmentListDTO> listUserAppointments(Authentication authentication, Pageable pageable){
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(()-> new NotFoundException("User not found"));
        Page<Appointment> page = repository.findByUser(user, pageable);
        if(page.isEmpty()){
            return Page.empty();
        }
        return page.map(mapper::toListDTO);
    }

    @Override
    public Page<AppointmentListDTO> listAdminAppointments(Authentication authentication, Pageable pageable){
        String username = authentication.getName();
        User admin = userRepository.findByUsername(username).orElseThrow(()-> new NotFoundException("User not found"));
        Page<Appointment> page = repository.findByAdmin(admin, pageable);
        if(page.isEmpty()){
            return Page.empty();
        }
        return page.map(mapper::toListDTO);
    }

    @Override
    @Transactional
    public void deleteAppointment(Long id){
        Appointment appointment = repository.findById(id).orElseThrow(()-> new NotFoundException("Appointment not found"));
        repository.delete(appointment);
        //DEVOLUCION DE DINERO
    }
}
