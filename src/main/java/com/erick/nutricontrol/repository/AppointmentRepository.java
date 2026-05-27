package com.erick.nutricontrol.repository;

import com.erick.nutricontrol._enum.AppointmentStatus;
import com.erick.nutricontrol.model.Appointment;
import com.erick.nutricontrol.security.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @EntityGraph(attributePaths = {"admin", "user"})
    Page<Appointment> findByAdminAndAppointmentStatusIn(User admin, List<AppointmentStatus> allAppointmentStatus, Pageable pageable);
    @EntityGraph(attributePaths = {"admin", "user"})
    Page<Appointment> findByUser(User user, Pageable pageable);
    @Query("SELECT a FROM Appointment a WHERE a.appointmentStatus = 'PENDING' AND a.endTimeUtc <= :now")
    List<Appointment> findExpiredAppointments(@Param("now") OffsetDateTime now);
    List<Appointment> findByAppointmentStatusAndCreatedAtBefore(AppointmentStatus appointmentStatus, OffsetDateTime dateTime);
    @EntityGraph(attributePaths = {"admin", "user"})
    List<Appointment> findByAdminInAndDateBetween(List<User> admins, LocalDate startDate, LocalDate endDate);
    @Query("SELECT a FROM Appointment a WHERE a.appointmentStatus = :status AND a.startTimeUtc BETWEEN :now AND :in24Hours")
    List<Appointment> findUpcomingAppointmentsToCapture(@Param("status") AppointmentStatus status, @Param("now") OffsetDateTime now, @Param("in24Hours") OffsetDateTime in24Hours);
    boolean existsByUserAndAdminAndDateAndStartTimeAndAppointmentStatusNot(User user, User admin, LocalDate date, LocalTime startTime, AppointmentStatus status);
    @Query("SELECT a FROM Appointment a WHERE a.appointmentStatus = 'CONFIRMED' AND a.endTimeUtc <= :now")
    List<Appointment> findPastConfirmedAppointments(@Param("now") OffsetDateTime now);
    @Query("SELECT a FROM Appointment a WHERE a.appointmentStatus = 'USER_DIDNT_COME' AND a.endTimeUtc <= :thirtyDaysAgo")
    List<Appointment> findOldUnattendedAppointments(@Param("thirtyDaysAgo") OffsetDateTime thirtyDaysAgo);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentStatus = :status AND a.startTimeUtc <= :windowEnd AND a.startTimeUtc > :now AND a.reminder24hSent = false")
    List<Appointment> findAppointmentsFor24hReminder(@Param("status") AppointmentStatus status, @Param("now") OffsetDateTime now, @Param("windowEnd") OffsetDateTime windowEnd);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentStatus = :status AND a.startTimeUtc <= :windowEnd AND a.startTimeUtc > :now AND a.reminder15mSent = false")
    List<Appointment> findAppointmentsFor15mReminder(@Param("status") AppointmentStatus status, @Param("now") OffsetDateTime now, @Param("windowEnd") OffsetDateTime windowEnd);
}