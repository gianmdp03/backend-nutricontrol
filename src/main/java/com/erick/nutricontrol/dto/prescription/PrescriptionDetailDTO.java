package com.erick.nutricontrol.dto.prescription;

public record PrescriptionDetailDTO(
    Long id,
    String patientName,
    String age,
    String textareaTexto,
    String adminName,
    String specialty,
    String exequatur,
    String date) {}
