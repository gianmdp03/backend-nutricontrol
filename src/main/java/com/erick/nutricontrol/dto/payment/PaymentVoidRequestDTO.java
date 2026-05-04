package com.erick.nutricontrol.dto.payment;

import jakarta.validation.constraints.NotBlank;

public record PaymentVoidRequestDTO(@NotBlank String authorizationId) {}
