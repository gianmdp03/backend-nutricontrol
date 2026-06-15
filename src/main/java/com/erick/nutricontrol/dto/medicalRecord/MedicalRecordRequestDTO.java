package com.erick.nutricontrol.dto.medicalRecord;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record MedicalRecordRequestDTO(
    @NotBlank @Size(min = 1, max = 3) String age,
    @NotNull @Positive Double weight,
    @NotNull @Positive Double height,
    @Size(max = 500) String medicalHistory,
    @Size(max = 300) String medication) {}
