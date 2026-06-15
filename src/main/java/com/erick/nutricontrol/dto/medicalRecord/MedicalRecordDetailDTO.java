package com.erick.nutricontrol.dto.medicalRecord;

import java.time.OffsetDateTime;

public record MedicalRecordDetailDTO(
    Long id,
    String age,
    Double weight,
    Double height,
    String medicalHistory,
    String medication,
    OffsetDateTime lastUpdateDate) {}
