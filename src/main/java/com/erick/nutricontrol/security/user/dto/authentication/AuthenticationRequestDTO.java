package com.erick.nutricontrol.security.user.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationRequestDTO(
    @NotBlank @Size(max = 100) String loginInput,
    @NotBlank @Size(min = 8, max = 64) String password) {}
