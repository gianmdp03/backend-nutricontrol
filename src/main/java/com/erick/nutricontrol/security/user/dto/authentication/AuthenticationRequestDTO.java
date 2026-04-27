package com.erick.nutricontrol.security.user.dto.authentication;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequestDTO(@NotBlank String loginInput, @NotBlank String password) {
}
