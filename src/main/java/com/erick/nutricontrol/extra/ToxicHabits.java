package com.erick.nutricontrol.extra;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ToxicHabits(
    @Size(max = 200) String coffee,
    @Size(max = 200) String alcohol,
    @Size(max = 200) String cigarettes,
    @Size(max = 200) String tea,
    @Size(max = 200) String drugs,
    @Min(0) int smokingIndex) {}
