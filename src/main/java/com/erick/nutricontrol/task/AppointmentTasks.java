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
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentTasks {
    private final AppointmentRepository repository;

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
