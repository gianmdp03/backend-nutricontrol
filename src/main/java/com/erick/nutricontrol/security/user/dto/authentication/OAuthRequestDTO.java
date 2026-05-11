package com.erick.nutricontrol.security.user.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record OAuthRequestDTO(@NotBlank @Email String email, @NotBlank String name, @NotBlank String lastname, String picture) {}
