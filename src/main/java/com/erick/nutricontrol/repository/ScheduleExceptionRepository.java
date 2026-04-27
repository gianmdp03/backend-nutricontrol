package com.erick.nutricontrol.repository;

import com.erick.nutricontrol.model.ScheduleException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ScheduleExceptionRepository extends JpaRepository<ScheduleException, Long> {
    boolean existsByDate(LocalDate date);
}
