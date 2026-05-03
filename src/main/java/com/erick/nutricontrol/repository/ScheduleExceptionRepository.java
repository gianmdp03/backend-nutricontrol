package com.erick.nutricontrol.repository;

import com.erick.nutricontrol.model.ScheduleException;
import com.erick.nutricontrol.security.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface ScheduleExceptionRepository extends JpaRepository<ScheduleException, Long> {
    boolean existsByDate(LocalDate date);
    Page<ScheduleException> findByAdmin(User admin, Pageable pageable);
}
