package com.erick.nutricontrol.dto.medicalHistoryTracking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MedicalHistoryTrackingRequestDTO(
    @NotBlank @Size(max = 2000) String consultationReason,
    @Size(max = 2000) String labResultsAndImages,
    @Size(max = 2000) String diagnosticImpression,
    @Size(max = 2000) String medicalPlan,
    @NotNull Long medicalHistoryId) {}
