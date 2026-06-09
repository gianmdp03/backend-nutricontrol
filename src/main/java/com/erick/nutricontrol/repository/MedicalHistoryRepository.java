package com.erick.nutricontrol.repository;

import com.erick.nutricontrol.model.MedicalHistory;
import com.erick.nutricontrol.security.user.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
  boolean existsByUserId(Long userId);

  Optional<MedicalHistory> findByUserId(Long userId);

  Optional<MedicalHistory> findByUserIdAndAdmin(Long userId, User admin);
}
