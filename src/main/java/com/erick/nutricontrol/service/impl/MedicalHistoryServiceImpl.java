package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.dto.medicalHistory.MedicalHistoryDetailDTO;
import com.erick.nutricontrol.dto.medicalHistory.MedicalHistoryRequestDTO;
import com.erick.nutricontrol.exception.ConflictException;
import com.erick.nutricontrol.exception.NotFoundException;
import com.erick.nutricontrol.extra.DatetimeConverter;
import com.erick.nutricontrol.mapper.MedicalHistoryMapper;
import com.erick.nutricontrol.mapper.MedicalHistoryTrackingMapper;
import com.erick.nutricontrol.model.MedicalHistory;
import com.erick.nutricontrol.model.MedicalHistoryTracking;
import com.erick.nutricontrol.repository.MedicalHistoryRepository;
import com.erick.nutricontrol.repository.MedicalHistoryTrackingRepository;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.security.user.repository.UserRepository;
import com.erick.nutricontrol.service.MedicalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MedicalHistoryServiceImpl implements MedicalHistoryService {
  private final MedicalHistoryRepository medicalHistoryRepository;
  private final MedicalHistoryTrackingRepository medicalHistoryTrackingRepository;
  private final MedicalHistoryMapper medicalHistoryMapper;
  private final MedicalHistoryTrackingMapper medicalHistoryTrackingMapper;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public MedicalHistoryDetailDTO createMedicalHistoryAndFirstTracking(
      User admin, MedicalHistoryRequestDTO dto) {
    User user =
        userRepository
            .findById(dto.userId())
            .orElseThrow(() -> new NotFoundException("User not found"));
    if (medicalHistoryRepository.existsByUser(user)) {
      throw new ConflictException(
          "Medical History already exists, please update it with its proper endpoint");
    }
    MedicalHistory medicalHistory = medicalHistoryMapper.toEntity(dto);
    medicalHistory.setUser(user);
    medicalHistory.setAdmin(admin);
    medicalHistory = medicalHistoryRepository.save(medicalHistory);

    MedicalHistoryTracking medicalHistoryTracking =
        medicalHistoryTrackingMapper.toEntity(dto.trackingDto());
    medicalHistoryTracking.setMedicalHistory(medicalHistory);
    medicalHistoryTrackingRepository.save(medicalHistoryTracking);

    medicalHistory.getTrackings().add(medicalHistoryTracking);

    String date =
        DatetimeConverter.convertFromUtcToTimezone(
            medicalHistoryTracking.getDatetime(), user.getTimezone());
  }
}
