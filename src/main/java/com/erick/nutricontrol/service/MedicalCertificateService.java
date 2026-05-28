package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.medicalCertificate.MedicalCertificateDetailDTO;
import com.erick.nutricontrol.dto.medicalCertificate.MedicalCertificateRequestDTO;
import com.erick.nutricontrol.security.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MedicalCertificateService {
  MedicalCertificateDetailDTO createMedicalCertificate(
      User admin, MedicalCertificateRequestDTO dto);

  Page<MedicalCertificateDetailDTO> getAllUserMedicalCertificate(User user, Pageable pageable);

  byte[] getPDFMedicalCertificate(User user, Long id);
}
