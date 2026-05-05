package com.erick.nutricontrol.task;

import com.erick.nutricontrol._enum.AppointmentStatus;
import com.erick.nutricontrol.model.Appointment;
import com.erick.nutricontrol.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentTasks {
    private final AppointmentRepository repository;

    @Scheduled(fixedRate = 900000)
    @Transactional
    public void cleanupUnpaidAppointments(){
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
        LocalDateTime threeDaysAgo =  LocalDateTime.now().minusDays(3);
        List<Appointment> toExpire = repository.findByStatusAndCreatedAtBefore(AppointmentStatus.PENDING, thirtyMinutesAgo);
        if(!toExpire.isEmpty()){
            toExpire.forEach(a -> a.setAppointmentStatus(AppointmentStatus.CANCELLED));
            repository.saveAll(toExpire);
            System.out.println("Se vencieron " + toExpire.size() + " turnos por falta de pago.");
        }

        List<Appointment> garbageToDestroy = repository.findByStatusAndCreatedAtBefore(AppointmentStatus.CANCELLED, threeDaysAgo);
        if(!garbageToDestroy.isEmpty()){
            repository.deleteAll(garbageToDestroy);
            System.out.println("Se eliminó físicamente la basura: " + garbageToDestroy.size() + " turnos viejos.");
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanFinishedAppointments(){
        log.info("Cleaning up finished appointments");

        LocalDate today = LocalDate.now();
        LocalTime now  = LocalTime.now();

        List<Appointment> expiredAppointments = repository.findExpiredAppointments(today, now);

        if (!expiredAppointments.isEmpty()) {
            for (Appointment appointment : expiredAppointments) {
                appointment.setAppointmentStatus(AppointmentStatus.CANCELLED);
            }

            repository.saveAll(expiredAppointments);
            log.info("Se limpiaron {} turnos vencidos.", expiredAppointments.size());
        } else {
            log.info("No hay turnos para limpiar.");
        }
    }
}
