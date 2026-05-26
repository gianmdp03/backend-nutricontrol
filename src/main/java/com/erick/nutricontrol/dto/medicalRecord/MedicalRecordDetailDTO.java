package com.erick.nutricontrol.dto.medicalRecord;

import java.time.OffsetDateTime;

public record MedicalRecordDetailDTO(
    Long id,
    Double weight,
    Double height,
    String medicalHistory,
    String medication,
    OffsetDateTime lastUpdateDate) {}
