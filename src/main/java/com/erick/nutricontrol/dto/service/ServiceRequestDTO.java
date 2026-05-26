package com.erick.nutricontrol.dto.service;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ServiceRequestDTO(
    @NotBlank @Size(max = 50) String name, @NotBlank @Size(max = 200) String description) {}
