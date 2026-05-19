package com.erick.nutricontrol.task;

import com.erick.nutricontrol._enum.AppointmentStatus;
import com.erick.nutricontrol._enum.PaymentStatus;
import com.erick.nutricontrol.model.Appointment;
import com.erick.nutricontrol.model.Payment;
import com.erick.nutricontrol.repository.AppointmentRepository;
import com.erick.nutricontrol.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentTasks {
    private final AppointmentRepository repository;
    private final PaymentService paymentService;

    @Scheduled(fixedRate = 900000)
    @Transactional
    public void cleanupUnpaidAppointments(){
        OffsetDateTime thirtyMinutesAgo = OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(30);
        OffsetDateTime threeDaysAgo =  OffsetDateTime.now(ZoneOffset.UTC).minusDays(3);
        List<Appointment> toExpire = repository.findByAppointmentStatusAndCreatedAtBefore(AppointmentStatus.PENDING, thirtyMinutesAgo);
        if(!toExpire.isEmpty()){
            toExpire.forEach(a -> a.setAppointmentStatus(AppointmentStatus.CANCELLED));
            repository.saveAll(toExpire);
            log.info("Se vencieron {} turnos por falta de pago.", toExpire.size());
        }

        List<Appointment> garbageToDestroy = repository.findByAppointmentStatusAndCreatedAtBefore(AppointmentStatus.CANCELLED, threeDaysAgo);
        if(!garbageToDestroy.isEmpty()){
            repository.deleteAll(garbageToDestroy);
            log.info("Se eliminó físicamente la basura: {} turnos viejos.", garbageToDestroy.size());
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanFinishedAppointments(){
        log.info("Limpiando turnos finalizados");

        OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);

        List<Appointment> expiredAppointments = repository.findExpiredAppointments(nowUtc);

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

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void autoCapturePayments24hBefore() {
        log.info("Buscando turnos a menos de 24hs para capturar el pago...");

        OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime in24HoursUtc = nowUtc.plusHours(24);

        List<Appointment> upcoming = repository.findUpcomingAppointmentsToCapture(AppointmentStatus.CONFIRMED, nowUtc, in24HoursUtc);

        int capturedCount = 0;

        for (Appointment app : upcoming) {
            if (!app.getPayments().isEmpty()) {
                for (Payment payment : app.getPayments()) {
                    if ("AUTHORIZED".equals(payment.getStatus().name()) && payment.getPaypalAuthorizationId() != null) {
                        try {
                            paymentService.capturePayment(payment.getPaypalAuthorizationId());
                            capturedCount++;
                        } catch (Exception e) {
                            log.error("Fallo al intentar capturar automáticamente el pago del turno ID: " + app.getId(), e);
                            payment.setStatus(PaymentStatus.FAILED);
                        }
                    }
                }
            }
        }

        if (capturedCount > 0) {
            log.info("Se capturaron exitosamente los fondos de {} turnos.", capturedCount);
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void markUnattendedAppointments() {
        log.info("Buscando turnos confirmados que ya pasaron y no fueron marcados como completados...");

        OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
        List<Appointment> pastAppointments = repository.findPastConfirmedAppointments(nowUtc);

        if (!pastAppointments.isEmpty()) {
            pastAppointments.forEach(a -> a.setAppointmentStatus(AppointmentStatus.USER_DIDNT_COME));
            repository.saveAll(pastAppointments);
            log.info("Se marcaron {} turnos como USER_DIDNT_COME.", pastAppointments.size());
        }
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteOldUnattendedAppointments() {
        log.info("Buscando turnos USER_DIDNT_COME de más de 30 días para eliminar...");

        OffsetDateTime thirtyDaysAgoUtc = OffsetDateTime.now(ZoneOffset.UTC).minusDays(30);
        List<Appointment> garbageToDestroy = repository.findOldUnattendedAppointments(thirtyDaysAgoUtc);

        if (!garbageToDestroy.isEmpty()) {
            repository.deleteAll(garbageToDestroy);
            log.info("Se eliminó físicamente la basura: {} turnos USER_DIDNT_COME con más de 30 días.", garbageToDestroy.size());
        }
    }
}
