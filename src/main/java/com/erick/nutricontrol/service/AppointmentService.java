package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.appointment.AppointmentDetailDTO;
import com.erick.nutricontrol.dto.appointment.AppointmentListDTO;
import com.erick.nutricontrol.dto.appointment.AppointmentRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface AppointmentService{
    AppointmentDetailDTO addAppointment(String username, AppointmentRequestDTO dto);
    Map<LocalDate, List<LocalTime>> getAvailableAppointments(Integer days, Integer minutesGap);
    Page<AppointmentListDTO> listUserAppointments(Authentication authentication, Pageable pageable);
    Page<AppointmentListDTO> listAdminAppointments(Authentication authentication, Pageable pageable);
    void deleteAppointment(Long id);
}
