package com.erick.nutricontrol.security.user.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
    @NotBlank @Size(max = 50) String name,
    @NotBlank @Size(max = 50) String lastname,
    @NotBlank @Size(max = 50) String username,
    @NotBlank @Email @Size(max = 100) String email,
    @NotBlank @Size(min = 6, max = 64) String password,
    @NotBlank @Size(max = 50) String timezone) {}
