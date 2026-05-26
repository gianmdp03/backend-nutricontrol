package com.erick.nutricontrol.dto.medicalRecord;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MedicalRecordRequestDTO(
    @NotBlank @Positive Double weight,
    @NotBlank @Positive Double height,
    @Size(max = 500) String medicalHistory,
    @Size(max = 300) String medication) {}
