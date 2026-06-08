package com.erick.nutricontrol.extra;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PatientData(
    @NotBlank @Size(max = 100) String fullName,
    @NotBlank @Size(max = 100) String nationalId,
    @NotNull @Min(0) Integer age,
    @NotNull @Size(max = 50) String gender,
    @Size(max = 100) String maritalStatus,
    @Size(max = 60) String address,
    @Size(max = 30) String phoneNumber,
    @Size(max = 100) String healthInsurance,
    @Size(max = 100) String occupation,
    @Size(max = 100) String emergencyContact) {}
