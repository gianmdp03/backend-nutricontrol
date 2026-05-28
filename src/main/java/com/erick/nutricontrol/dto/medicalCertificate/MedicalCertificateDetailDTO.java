package com.erick.nutricontrol.dto.medicalCertificate;

public record MedicalCertificateDetailDTO(
    Long id,
    String patientName,
    String age,
    String textareaTexto,
    String adminName,
    String specialty,
    String exequatur,
    String date) {}
