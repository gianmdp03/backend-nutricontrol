package com.erick.nutricontrol.task;

import com.erick.nutricontrol.model.ScheduleException;
import com.erick.nutricontrol.repository.ScheduleExceptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduleExceptionTasks {
    private final ScheduleExceptionRepository repository;

    @Scheduled(cron = "0 0 0 * * *", zone = "America/Santo_Domingo")
    @Transactional
    public void deleteOldScheduleExceptions() {
        log.info("Buscando excepciones de horario pasadas para eliminar (Hora RD)...");

        ZoneId dominicanRepublicZone = ZoneId.of("America/Santo_Domingo");
        LocalDate todayDR = LocalDate.now(dominicanRepublicZone);

        List<ScheduleException> pastExceptions = repository.findByDateBefore(todayDR);

        if (!pastExceptions.isEmpty()) {
            repository.deleteAll(pastExceptions);
            log.info("Se eliminó físicamente la basura: {} excepciones de horario.", pastExceptions.size());
        } else {
            log.info("No hay excepciones de horario pasadas para limpiar.");
        }
    }
}