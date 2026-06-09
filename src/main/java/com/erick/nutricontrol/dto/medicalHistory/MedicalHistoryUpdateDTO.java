package com.erick.nutricontrol.dto.medicalHistory;

import com.erick.nutricontrol.extra.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record MedicalHistoryUpdateDTO(
    @Valid PatientData patientData,
    @Size(max = 1000) String allergies,
    @Size(max = 2000) String currentIllnessHistory,
    @Valid ToxicHabits toxicHabits,
    @Valid FamilyHistory familyHistory,
    @Valid SystemReview systemReview,
    @Valid VitalSigns vitalSigns) {}
