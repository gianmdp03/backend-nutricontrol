package com.erick.nutricontrol.repository;

import com.erick.nutricontrol.model.ScheduleException;
import com.erick.nutricontrol.security.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleExceptionRepository extends JpaRepository<ScheduleException, Long> {
    boolean existsByDate(LocalDate date);
    @EntityGraph(attributePaths = {"admin"})
    Page<ScheduleException> findByAdmin(User admin, Pageable pageable);
    List<ScheduleException> findByAdminIn(List<User> admins);
    List<ScheduleException> findByDateBefore(LocalDate date);
}
