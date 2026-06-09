package com.erick.nutricontrol.dto.medicalHistory;

import com.erick.nutricontrol.dto.medicalHistoryTracking.MedicalHistoryTrackingDetailDTO;
import com.erick.nutricontrol.extra.*;
import java.util.List;
import lombok.Builder;

@Builder
public record MedicalHistoryDetailDTO(
    Long id,
    List<MedicalHistoryTrackingDetailDTO> trackings,
    PatientData patientData,
    String allergies,
    String currentIllnessHistory,
    ToxicHabits toxicHabits,
    FamilyHistory familyHistory,
    SystemReview systemReview,
    VitalSigns vitalSigns) {}
