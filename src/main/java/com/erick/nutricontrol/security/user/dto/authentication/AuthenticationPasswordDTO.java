package com.erick.nutricontrol.security.user.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationPasswordDTO(
    @NotBlank @Email @Size(max = 100) String email,
    @Size(min = 6, max = 64) String password,
    @Size(max = 10) String token) {}
