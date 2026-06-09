package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.medicalHistory.MedicalHistoryDetailDTO;
import com.erick.nutricontrol.dto.medicalHistory.MedicalHistoryRequestDTO;
import com.erick.nutricontrol.dto.medicalHistory.MedicalHistoryUpdateDTO;
import com.erick.nutricontrol.dto.medicalHistoryTracking.MedicalHistoryTrackingRequestDTO;
import com.erick.nutricontrol.security.user.model.User;

public interface MedicalHistoryService {
  MedicalHistoryDetailDTO createMedicalHistoryAndFirstTracking(
      User admin, MedicalHistoryRequestDTO dto);

  MedicalHistoryDetailDTO addMedicalHistoryTrackingToExistingHistory(
      Long userId, User admin, MedicalHistoryTrackingRequestDTO dto);

  MedicalHistoryDetailDTO getUserMedicalHistory(Long userId, User admin);

  Boolean checkMedicalHistory(Long userId);

  MedicalHistoryDetailDTO updateMedicalHistory(
      Long userId, User admin, MedicalHistoryUpdateDTO dto);

  byte[] getPDFMedicalHistory(Long userId, Long trackingId);
}
