package com.erick.nutricontrol.dto.medicalRecord;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MedicalRecordRequestDTO(
    @NotNull @Positive Double weight,
    @NotNull @Positive Double height,
    @Size(max = 500) String medicalHistory,
    @Size(max = 300) String medication) {}
