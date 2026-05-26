package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.appointment.AppointmentDetailDTO;
import com.erick.nutricontrol.dto.appointment.AppointmentRequestDTO;
import com.erick.nutricontrol.dto.appointment.AvailableSlotDTO;
import com.erick.nutricontrol.dto.payment.PaymentOrderResponseDTO;
import com.erick.nutricontrol.security.user.model.User;
import com.paypal.sdk.exceptions.ApiException;
import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {
    PaymentOrderResponseDTO addAppointment(User user, AppointmentRequestDTO dto) throws IOException, ApiException;
    List<AvailableSlotDTO> getAvailableAppointments();
    Page<AppointmentDetailDTO> listUserAppointments(User user, Pageable pageable);
    Page<AppointmentDetailDTO> listAdminAppointments(User user, Pageable pageable);
    void deleteAppointment(Long id, User user);
    void adminDeleteAppointment(Long id, boolean refund);
}
