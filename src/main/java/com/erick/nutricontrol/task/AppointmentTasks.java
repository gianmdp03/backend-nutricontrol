package com.erick.nutricontrol.task;

import com.erick.nutricontrol._enum.AppointmentStatus;
import com.erick.nutricontrol._enum.NotificationType;
import com.erick.nutricontrol.model.Appointment;
import com.erick.nutricontrol.model.Notification;
import com.erick.nutricontrol.model.Payment;
import com.erick.nutricontrol.repository.AppointmentRepository;
import com.erick.nutricontrol.service.EmailService;
import com.erick.nutricontrol.service.NotificationService;
import com.erick.nutricontrol.service.PaymentService;
import java.time.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentTasks {
  private final AppointmentRepository repository;
  private final PaymentService paymentService;
  private final EmailService emailService;
  private final NotificationService notificationService;

  @Scheduled(fixedRate = 900000)
  @Transactional
  public void cleanupUnpaidAppointments() {
    OffsetDateTime thirtyMinutesAgo = OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(30);
    OffsetDateTime threeDaysAgo = OffsetDateTime.now(ZoneOffset.UTC).minusDays(3);
    List<Appointment> toExpire =
        repository.findByAppointmentStatusAndCreatedAtBefore(
            AppointmentStatus.PENDING, thirtyMinutesAgo);
    if (!toExpire.isEmpty()) {
      toExpire.forEach(a -> a.setAppointmentStatus(AppointmentStatus.CANCELLED));
      repository.saveAll(toExpire);
      log.info("Se vencieron {} turnos por falta de pago.", toExpire.size());
    }

    List<Appointment> garbageToDestroy =
        repository.findByAppointmentStatusAndCreatedAtBefore(
            AppointmentStatus.CANCELLED, threeDaysAgo);
    if (!garbageToDestroy.isEmpty()) {
      repository.deleteAll(garbageToDestroy);
      log.info("Se eliminó físicamente la basura: {} turnos viejos.", garbageToDestroy.size());
    }
  }

  @Scheduled(cron = "0 0 * * * *")
  @Transactional
  public void cleanFinishedAppointments() {
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

    List<Appointment> upcoming =
        repository.findUpcomingAppointmentsToCapture(
            AppointmentStatus.CONFIRMED, nowUtc, in24HoursUtc);

    int capturedCount = 0;

    for (Appointment app : upcoming) {
      if (!app.getPayments().isEmpty()) {
        for (Payment payment : app.getPayments()) {
          if ("AUTHORIZED".equals(payment.getStatus().name())
              && payment.getPaypalAuthorizationId() != null) {
            paymentService.capturePaymentAsync(payment.getPaypalAuthorizationId(), app.getId());
            capturedCount++;
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

  @Scheduled(cron = "0 0 3 * * *", zone = "America/Santo_Domingo")
  @Transactional
  public void deleteOldUnattendedAppointments() {
    log.info("Buscando turnos USER_DIDNT_COME de más de 30 días para eliminar...");

    OffsetDateTime thirtyDaysAgoUtc = OffsetDateTime.now(ZoneOffset.UTC).minusDays(30);
    List<Appointment> garbageToDestroy = repository.findOldUnattendedAppointments(thirtyDaysAgoUtc);

    if (!garbageToDestroy.isEmpty()) {
      repository.deleteAll(garbageToDestroy);
      log.info(
          "Se eliminó físicamente la basura: {} turnos USER_DIDNT_COME con más de 30 días.",
          garbageToDestroy.size());
    }
  }

  @Scheduled(cron = "0 */15 * * * *")
  @Transactional
  public void send24hReminders() {
    OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
    OffsetDateTime in24HoursUtc = nowUtc.plusHours(24);

    List<Appointment> upcoming =
        repository.findAppointmentsFor24hReminder(
            AppointmentStatus.CONFIRMED, nowUtc, in24HoursUtc);

    int emailsSent = 0;
    for (Appointment app : upcoming) {
      // EMAIL
      Map<String, Object> variables = new HashMap<>();
      variables.put("patientName", app.getUser().getName());
      variables.put("doctorName", app.getAdmin().getName() + " " + app.getAdmin().getLastname());
      variables.put("appointmentTime", app.getStartTime().toString() + " hs");

      emailService.sendHtmlTemplateEmail(
          app.getUser().getEmail(), "Recordatorio: Tu turno es mañana", "reminder-24h", variables);

      // NOTIFICACIÓN
      Notification notification =
          Notification.builder()
              .message(
                  "Te recordamos que mañana tenés un turno programado con "
                      + app.getAdmin().getName()
                      + ".")
              .type(NotificationType.USER_APPOINTMENT_REMINDER)
              .build();

      notificationService.createNotification(app.getUser(), notification);
      app.setReminder24hSent(true);
      emailsSent++;
    }

    if (emailsSent > 0) {
      log.info("Se enviaron {} recordatorios y notificaciones de 24 horas.", emailsSent);
    }
  }

  @Scheduled(cron = "0 * * * * *")
  @Transactional
  public void send15mReminders() {
    OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
    OffsetDateTime in16MinutesUtc = nowUtc.plusMinutes(16);

    List<Appointment> upcoming =
        repository.findAppointmentsFor15mReminder(
            AppointmentStatus.CONFIRMED, nowUtc, in16MinutesUtc);

    int emailsSent = 0;
    for (Appointment app : upcoming) {
      if (app.getMeetingLink() != null) {
        // EMAIL
        Map<String, Object> variables = new HashMap<>();
        variables.put("patientName", app.getUser().getName());
        variables.put("doctorName", app.getAdmin().getName() + " " + app.getAdmin().getLastname());
        variables.put("meetLink", app.getMeetingLink());

        emailService.sendHtmlTemplateEmail(
            app.getUser().getEmail(),
            "¡Tu consulta empieza en 15 minutos!",
            "reminder-15m",
            variables);

        // NOTIFICACIÓN
        Notification notification =
            Notification.builder()
                .message(
                    "¡Preparate! Tu videollamada con "
                        + app.getAdmin().getName()
                        + " está por comenzar en 15 minutos.")
                .type(NotificationType.USER_APPOINTMENT_REMINDER)
                .build();

        notificationService.createNotification(app.getUser(), notification);
        app.setReminder15mSent(true);
        emailsSent++;
      }
    }

    if (emailsSent > 0) {
      log.info(
          "Se enviaron {} recordatorios urgentes HTML y notificaciones (15 minutos).", emailsSent);
    }
  }
}
