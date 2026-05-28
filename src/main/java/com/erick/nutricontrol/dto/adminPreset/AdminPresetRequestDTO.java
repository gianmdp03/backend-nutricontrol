package com.erick.nutricontrol.dto.adminPreset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminPresetRequestDTO(
    @NotBlank @Size(max = 50) String adminName,
    @NotBlank @Size(max = 50) String specialty,
    @NotBlank @Size(max = 10) String exequatur) {}
