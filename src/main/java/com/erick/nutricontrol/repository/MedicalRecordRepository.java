package com.erick.nutricontrol.repository;

import com.erick.nutricontrol.model.MedicalRecord;
import com.erick.nutricontrol.security.user.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
  Optional<MedicalRecord> findByUser(User user);
}
