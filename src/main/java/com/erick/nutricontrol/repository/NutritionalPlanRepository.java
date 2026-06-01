package com.erick.nutricontrol.repository;

import com.erick.nutricontrol.model.NutritionalPlan;
import com.erick.nutricontrol.security.user.model.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutritionalPlanRepository extends JpaRepository<NutritionalPlan, Long> {
    Page<NutritionalPlan> findByUser(User user, Pageable pageable);

    Page<NutritionalPlan> findByUserIsNull(Pageable pageable);

    Optional<NutritionalPlan> findByIdAndUser(Long id, User user);

    Optional<NutritionalPlan> findByIdAndUserIsNull(Long id);
}
