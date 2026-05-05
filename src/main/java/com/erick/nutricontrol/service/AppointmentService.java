package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.appointment.AppointmentDetailDTO;
import com.erick.nutricontrol.dto.appointment.AppointmentRequestDTO;
import com.erick.nutricontrol.dto.appointment.AvailableSlotDTO;
import com.erick.nutricontrol.dto.payment.PaymentOrderResponseDTO;
import com.paypal.sdk.exceptions.ApiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public interface AppointmentService{
    PaymentOrderResponseDTO addAppointment(String username, AppointmentRequestDTO dto) throws IOException, ApiException;
    List<AvailableSlotDTO> getAvailableAppointments();
    Page<AppointmentDetailDTO> listUserAppointments(String username, Pageable pageable);
    Page<AppointmentDetailDTO> listAdminAppointments(String username, Pageable pageable);
    void deleteAppointment(Long id);
    void adminDeleteAppointment(Long id, boolean refund);
}
