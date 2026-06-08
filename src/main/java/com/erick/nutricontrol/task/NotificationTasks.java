package com.erick.nutricontrol.task;

import com.erick.nutricontrol.repository.NotificationRepository;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationTasks {
  private final NotificationRepository repository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void runTasksOnStartup() {
        log.info("Iniciando chequeo post-reinicio en NotificationTasks...");
        this.deleteOldReadNotifications();
    }

  @Scheduled(cron = "0 0 3 * * *", zone = "America/Santo_Domingo")
  @Transactional
  public void deleteOldReadNotifications() {
    log.info("Cron iniciado: Buscando notificaciones leídas antiguas para eliminar...");

    OffsetDateTime sevenDaysAgoUtc = OffsetDateTime.now(ZoneOffset.UTC).minusDays(7);
    int deletedCount = repository.deleteReadNotificationsOlderThan(sevenDaysAgoUtc);

    if (deletedCount > 0) {
      log.info(
          "Mantenimiento exitoso: Se eliminaron físicamente {} notificaciones leídas de más de 7 días.",
          deletedCount);
    } else {
      log.info("No se encontraron notificaciones leídas antiguas para borrar.");
    }
  }
}
