package com.erick.nutricontrol.repository;

import com.erick.nutricontrol.model.MedicalHistory;
import com.erick.nutricontrol.security.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
  boolean existsByUser(User user);
}
