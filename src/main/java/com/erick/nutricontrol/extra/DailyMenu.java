package com.erick.nutricontrol.extra;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DailyMenu(
    @NotBlank @Size(max = 1000) String breakfast,
    @NotBlank @Size(max = 1000) String lunch,
    @NotBlank @Size(max = 1000) String dinner) {}
