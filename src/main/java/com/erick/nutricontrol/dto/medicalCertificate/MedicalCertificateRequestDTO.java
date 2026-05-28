package com.erick.nutricontrol.dto.medicalCertificate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MedicalCertificateRequestDTO(
    @NotBlank @Size(max = 50) String patientName,
    @NotBlank @Size(max = 5) String age,
    @NotBlank @Size(max = 2000) String textareaTexto,
    Long userId) {}
