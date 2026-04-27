package com.erick.nutricontrol.repository;

import com.erick.nutricontrol._enum.AppointmentStatus;
import com.erick.nutricontrol.model.Appointment;
import com.erick.nutricontrol.security.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDateBetween(LocalDate startDate, LocalDate endDate);
    boolean existsByDateAndStartTimeAndAppointmentStatusNot(LocalDate startDate, LocalTime startTime, AppointmentStatus appointmentStatus);
    Page<Appointment> findByAdminId(Long adminId, Pageable pageable);
    Page<Appointment> findByUser(User user, Pageable pageable);
}