package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.medicalRecord.MedicalRecordDetailDTO;
import com.erick.nutricontrol.dto.medicalRecord.MedicalRecordRequestDTO;
import com.erick.nutricontrol.security.user.model.User;

public interface MedicalRecordService {
  MedicalRecordDetailDTO saveOrUpdateMedicalRecord(User user, MedicalRecordRequestDTO dto);

  MedicalRecordDetailDTO getUserMedicalRecord(User user);

  MedicalRecordDetailDTO getPatientMedicalRecord(Long patientId);
}
