package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.dto.medicalRecord.MedicalRecordDetailDTO;
import com.erick.nutricontrol.dto.medicalRecord.MedicalRecordRequestDTO;
import com.erick.nutricontrol.exception.NotFoundException;
import com.erick.nutricontrol.mapper.MedicalRecordMapper;
import com.erick.nutricontrol.model.MedicalRecord;
import com.erick.nutricontrol.repository.MedicalRecordRepository;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.security.user.repository.UserRepository;
import com.erick.nutricontrol.service.MedicalRecordService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicalRecordServiceImpl implements MedicalRecordService {
  private final MedicalRecordRepository repository;
  private final MedicalRecordMapper mapper;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public MedicalRecordDetailDTO saveOrUpdateMedicalRecord(User user, MedicalRecordRequestDTO dto) {
    Optional<MedicalRecord> existingRecord = repository.findByUser(user);
    MedicalRecord recordToSave;

    if (existingRecord.isPresent()) {
      recordToSave = existingRecord.get();
      recordToSave.setWeight(dto.weight());
      recordToSave.setHeight(dto.height());
      recordToSave.setMedicalHistory(dto.medicalHistory());
      recordToSave.setMedication(dto.medication());
    } else {
      recordToSave =
          new MedicalRecord(
              dto.weight(), dto.height(), dto.medicalHistory(), dto.medication(), user);
    }

    recordToSave = repository.save(recordToSave);
    return mapper.toDetailDTO(recordToSave);
  }

  @Override
  public MedicalRecordDetailDTO getUserMedicalRecord(User user) {
    return getMedicalRecord(user);
  }

  @Override
  public MedicalRecordDetailDTO getPatientMedicalRecord(Long patientId) {
    User user =
        userRepository
            .findById(patientId)
            .orElseThrow(() -> new NotFoundException("User not found"));
    return getMedicalRecord(user);
  }

  private MedicalRecordDetailDTO getMedicalRecord(User user) {
    MedicalRecord medicalRecord =
        repository
            .findByUser(user)
            .orElseThrow(() -> new NotFoundException("Medical Record not found"));
    return mapper.toDetailDTO(medicalRecord);
  }
}
