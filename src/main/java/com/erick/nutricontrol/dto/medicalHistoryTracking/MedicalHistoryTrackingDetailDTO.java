package com.erick.nutricontrol.dto.medicalHistoryTracking;

public record MedicalHistoryTrackingDetailDTO(
    Long id,
    String consultationReason,
    String labResultsAndImages,
    String diagnosticImpression,
    String medicalPlan,
    String datetime) {}
