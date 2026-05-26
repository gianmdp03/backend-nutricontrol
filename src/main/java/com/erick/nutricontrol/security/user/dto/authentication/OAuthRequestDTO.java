package com.erick.nutricontrol.security.user.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OAuthRequestDTO(
    @NotBlank @Email @Size(max = 100) String email,
    @NotBlank @Size(max = 50) String name,
    @NotBlank @Size(max = 50) String lastname,
    @Size(max = 50) String picture) {}
