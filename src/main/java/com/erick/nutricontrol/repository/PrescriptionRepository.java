package com.erick.nutricontrol.repository;

import com.erick.nutricontrol.model.Prescription;
import com.erick.nutricontrol.security.user.model.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
  Page<Prescription> findByUser(User user, Pageable pageable);
  Page<Prescription> findByUserIsNull(Pageable pageable);
  Optional<Prescription> findByIdAndUser(Long id, User user);
  Optional<Prescription> findByIdAndUserIsNull(Long id);
}
