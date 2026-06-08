package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.medicalHistory.MedicalHistoryDetailDTO;
import com.erick.nutricontrol.dto.medicalHistory.MedicalHistoryRequestDTO;
import com.erick.nutricontrol.security.user.model.User;

public interface MedicalHistoryService {
    MedicalHistoryDetailDTO createMedicalHistoryAndFirstTracking(User admin, MedicalHistoryRequestDTO dto);
}
