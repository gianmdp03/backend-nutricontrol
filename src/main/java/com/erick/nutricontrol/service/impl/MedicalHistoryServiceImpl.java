package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.dto.medicalHistory.MedicalHistoryDetailDTO;
import com.erick.nutricontrol.dto.medicalHistory.MedicalHistoryRequestDTO;
import com.erick.nutricontrol.dto.medicalHistory.MedicalHistoryUpdateDTO;
import com.erick.nutricontrol.dto.medicalHistoryTracking.MedicalHistoryTrackingDetailDTO;
import com.erick.nutricontrol.dto.medicalHistoryTracking.MedicalHistoryTrackingRequestDTO;
import com.erick.nutricontrol.exception.ConflictException;
import com.erick.nutricontrol.exception.NotFoundException;
import com.erick.nutricontrol.extra.DatetimeConverter;
import com.erick.nutricontrol.model.MedicalHistory;
import com.erick.nutricontrol.model.MedicalHistoryTracking;
import com.erick.nutricontrol.repository.MedicalHistoryRepository;
import com.erick.nutricontrol.repository.MedicalHistoryTrackingRepository;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.security.user.repository.UserRepository;
import com.erick.nutricontrol.service.MedicalHistoryService;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MedicalHistoryServiceImpl implements MedicalHistoryService {
  private final MedicalHistoryRepository medicalHistoryRepository;
  private final MedicalHistoryTrackingRepository medicalHistoryTrackingRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public MedicalHistoryDetailDTO createMedicalHistoryAndFirstTracking(
      User admin, MedicalHistoryRequestDTO dto) {
    User user =
        userRepository
            .findById(dto.userId())
            .orElseThrow(() -> new NotFoundException("User not found"));
    if (medicalHistoryRepository.existsByUserId(user.getId())) {
      throw new ConflictException(
          "Medical History already exists, please update it with its proper endpoint");
    }
    MedicalHistory medicalHistory = toEntity(dto);
    medicalHistory.setUser(user);
    medicalHistory.setAdmin(admin);
    medicalHistory = medicalHistoryRepository.save(medicalHistory);

    MedicalHistoryTracking medicalHistoryTracking = toEntity(dto.trackingDto());
    medicalHistoryTracking.setMedicalHistory(medicalHistory);
    medicalHistoryTrackingRepository.save(medicalHistoryTracking);

    medicalHistory.getTrackings().add(medicalHistoryTracking);

    return toDetailDTO(medicalHistory);
  }

  @Override
  @Transactional
  public MedicalHistoryDetailDTO addMedicalHistoryTrackingToExistingHistory(
      Long userId, User admin, MedicalHistoryTrackingRequestDTO dto) {
    MedicalHistory medicalHistory =
        medicalHistoryRepository
            .findByUserIdAndAdmin(userId, admin)
            .orElseThrow(
                () -> new NotFoundException("User hasn't got medical history, please create it"));
    MedicalHistoryTracking medicalHistoryTracking = toEntity(dto);
    medicalHistoryTracking.setMedicalHistory(medicalHistory);
    medicalHistoryTrackingRepository.save(medicalHistoryTracking);
    medicalHistory.getTrackings().add(medicalHistoryTracking);
    return toDetailDTO(medicalHistory);
  }

  @Override
  public MedicalHistoryDetailDTO getUserMedicalHistory(Long userId, User admin) {
    MedicalHistory medicalHistory =
        medicalHistoryRepository
            .findByUserIdAndAdmin(userId, admin)
            .orElseThrow(
                () -> new NotFoundException("User hasn't got medical history, please create it"));
    return toDetailDTO(medicalHistory);
  }

  @Override
  public Boolean checkMedicalHistory(Long userId) {
    return medicalHistoryRepository.existsByUserId(userId);
  }

  @Override
  @Transactional
  public MedicalHistoryDetailDTO updateMedicalHistory(
      Long userId, User admin, MedicalHistoryUpdateDTO dto) {
    MedicalHistory medicalHistory =
        medicalHistoryRepository
            .findByUserIdAndAdmin(userId, admin)
            .orElseThrow(
                () -> new NotFoundException("User hasn't got medical history, please create it"));
    updateMedicalHistory(dto, medicalHistory);
    medicalHistory = medicalHistoryRepository.save(medicalHistory);
    return toDetailDTO(medicalHistory);
  }

  private MedicalHistoryDetailDTO toDetailDTO(MedicalHistory entity) {

    return MedicalHistoryDetailDTO.builder()
        .id(entity.getId())
        .trackings(toDtoList(entity.getTrackings()))
        .patientData(entity.getPatientData())
        .allergies(entity.getAllergies())
        .currentIllnessHistory(entity.getCurrentIllnessHistory())
        .toxicHabits(entity.getToxicHabits())
        .familyHistory(entity.getFamilyHistory())
        .systemReview(entity.getSystemReview())
        .vitalSigns(entity.getVitalSigns())
        .build();
  }

  private MedicalHistory toEntity(MedicalHistoryRequestDTO dto) {
    return MedicalHistory.builder()
        .patientData(dto.patientData())
        .allergies(dto.allergies())
        .currentIllnessHistory(dto.currentIllnessHistory())
        .toxicHabits(dto.toxicHabits())
        .familyHistory(dto.familyHistory())
        .systemReview(dto.systemReview())
        .vitalSigns(dto.vitalSigns())
        .build();
  }

  private MedicalHistoryTrackingDetailDTO toDetailDTO(
      MedicalHistoryTracking entity, String datetime) {
    return MedicalHistoryTrackingDetailDTO.builder()
        .id(entity.getId())
        .consultationReason(entity.getConsultationReason())
        .labResultsAndImages(entity.getLabResultsAndImages())
        .diagnosticImpression(entity.getDiagnosticImpression())
        .medicalPlan(entity.getMedicalPlan())
        .datetime(datetime)
        .build();
  }

  private MedicalHistoryTracking toEntity(MedicalHistoryTrackingRequestDTO dto) {
    return MedicalHistoryTracking.builder()
        .consultationReason(dto.consultationReason())
        .labResultsAndImages(dto.labResultsAndImages())
        .diagnosticImpression(dto.diagnosticImpression())
        .medicalPlan(dto.medicalPlan())
        .build();
  }

  private List<MedicalHistoryTrackingDetailDTO> toDtoList(Set<MedicalHistoryTracking> entity) {
    return entity.stream()
        .sorted(Comparator.comparing(MedicalHistoryTracking::getDatetime).reversed())
        .map(
            tracking -> {
              String formattedDate =
                  DatetimeConverter.convertFromUtcToTimezone(
                      tracking.getDatetime(), "America/Santo_Domingo");
              return MedicalHistoryTrackingDetailDTO.builder()
                  .id(tracking.getId())
                  .consultationReason(tracking.getConsultationReason())
                  .labResultsAndImages(tracking.getLabResultsAndImages())
                  .diagnosticImpression(tracking.getDiagnosticImpression())
                  .medicalPlan(tracking.getMedicalPlan())
                  .datetime(formattedDate)
                  .build();
            })
        .toList();
  }

  private void updateMedicalHistory(MedicalHistoryUpdateDTO dto, MedicalHistory entity) {
    Optional.ofNullable(dto.patientData()).ifPresent(entity::setPatientData);
    Optional.ofNullable(dto.allergies()).ifPresent(entity::setAllergies);
    Optional.ofNullable(dto.currentIllnessHistory()).ifPresent(entity::setCurrentIllnessHistory);
    Optional.ofNullable(dto.toxicHabits()).ifPresent(entity::setToxicHabits);
    Optional.ofNullable(dto.familyHistory()).ifPresent(entity::setFamilyHistory);
    Optional.ofNullable(dto.systemReview()).ifPresent(entity::setSystemReview);
    Optional.ofNullable(dto.vitalSigns()).ifPresent(entity::setVitalSigns);
  }
}
