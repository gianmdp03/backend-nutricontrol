package com.erick.nutricontrol.extra;

import jakarta.validation.constraints.Size;

public record VitalSigns(
    @Size(max = 100) String bloodPressure,
    Integer heartRate,
    Integer respiratoryRate,
    Double temperature,
    Integer oxygenSaturation,
    Double weight,
    Double heightSquared,
    Double bmi,
    Double waistCircumference,
    Double hipCircumference,
    Double whr) {}
