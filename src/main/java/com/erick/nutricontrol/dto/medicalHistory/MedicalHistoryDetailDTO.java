package com.erick.nutricontrol.dto.medicalHistory;

import com.erick.nutricontrol.dto.medicalHistoryTracking.MedicalHistoryTrackingDetailDTO;
import com.erick.nutricontrol.extra.*;
import java.util.Set;

public record MedicalHistoryDetailDTO(
    Long id,
    Set<MedicalHistoryTrackingDetailDTO> trackings,
    PatientData patientData,
    String allergies,
    String consultationReason,
    String currentIllnessHistory,
    ToxicHabits toxicHabits,
    FamilyHistory familyHistory,
    SystemReview systemReview,
    VitalSigns vitalSigns,
    String labResultsAndImages,
    String diagnosticImpression,
    String medicalPlan) {}
