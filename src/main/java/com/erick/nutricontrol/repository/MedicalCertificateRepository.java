package com.erick.nutricontrol.repository;

import com.erick.nutricontrol.model.MedicalCertificate;
import com.erick.nutricontrol.security.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicalCertificateRepository extends JpaRepository<MedicalCertificate, Long> {
  Page<MedicalCertificate> findByUser(User user, Pageable pageable);

  Page<MedicalCertificate> findByUserIsNull(Pageable pageable);

  Optional<MedicalCertificate> findByIdAndUser(Long id, User user);

  Optional<MedicalCertificate> findByIdAndUserIsNull(Long id);
}
