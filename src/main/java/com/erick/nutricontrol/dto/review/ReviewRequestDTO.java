package com.erick.nutricontrol.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequestDTO(
    @NotNull Long appointmentId,
    @NotNull @Min(1) @Max(5) int score,
    @Size(max = 1000) String comment) {}
