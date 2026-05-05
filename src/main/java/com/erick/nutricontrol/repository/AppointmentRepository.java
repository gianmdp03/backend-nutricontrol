package com.erick.nutricontrol.repository;

import com.erick.nutricontrol._enum.AppointmentStatus;
import com.erick.nutricontrol.model.Appointment;
import com.erick.nutricontrol.security.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDateBetween(LocalDate startDate, LocalDate endDate);
    boolean existsByDateAndStartTimeAndAppointmentStatusNot(LocalDate startDate, LocalTime startTime, AppointmentStatus appointmentStatus);
    Page<Appointment> findByAdmin(User admin, Pageable pageable);
    Page<Appointment> findByUser(User user, Pageable pageable);
    @Query("SELECT a FROM Appointment a WHERE a.appointmentStatus = 'PENDING' AND (a.date < :today OR (a.date = :today AND a.endTime <= :now))")
    List<Appointment> findExpiredAppointments(@Param("today") LocalDate today, @Param("now") LocalTime now);
    List<Appointment> findByStatusAndCreatedAtBefore(AppointmentStatus appointmentStatus, LocalDateTime dateTime);
    Page<Appointment> findByAdminAndDateBetween(User admin,  LocalDate startDate, LocalDate endDate, Pageable pageable);
}