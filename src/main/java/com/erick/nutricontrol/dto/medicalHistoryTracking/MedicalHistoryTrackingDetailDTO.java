package com.erick.nutricontrol.dto.medicalHistoryTracking;

import lombok.Builder;

@Builder
public record MedicalHistoryTrackingDetailDTO(
    Long id,
    String consultationReason,
    String labResultsAndImages,
    String diagnosticImpression,
    String medicalPlan,
    String datetime) {}
