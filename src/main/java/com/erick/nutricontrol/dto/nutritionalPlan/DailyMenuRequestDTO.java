package com.erick.nutricontrol.dto.nutritionalPlan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DailyMenuRequestDTO(
    @NotBlank @Size(max = 1000) String breakfast,
    @NotBlank @Size(max = 1000) String lunch,
    @NotBlank @Size(max = 1000) String dinner) {}
