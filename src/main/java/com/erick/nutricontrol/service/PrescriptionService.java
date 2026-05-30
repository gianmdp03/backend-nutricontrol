package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.prescription.PrescriptionDetailDTO;
import com.erick.nutricontrol.dto.prescription.PrescriptionRequestDTO;
import com.erick.nutricontrol.security.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PrescriptionService {
  PrescriptionDetailDTO createPrescription(User admin, PrescriptionRequestDTO dto);

  Page<PrescriptionDetailDTO> getAllUserPrescriptions(User user, Pageable pageable);

  Page<PrescriptionDetailDTO> adminGetUserPrescriptions(Long userId, Pageable pageable);

  PrescriptionDetailDTO createManualPrescription(User admin, PrescriptionRequestDTO dto);

  Page<PrescriptionDetailDTO> getManualPrescriptions(Pageable pageable);

  byte[] getPDFPrescription(User user, Long id);

  byte[] getManualPDFPrescription(Long id);
}
