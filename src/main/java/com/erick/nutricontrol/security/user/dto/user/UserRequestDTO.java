package com.erick.nutricontrol.security.user.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequestDTO(@NotBlank String name,
                             @NotBlank String lastname,
                             @NotBlank String username,
                             @NotBlank @Email String email,
                             @NotBlank String password,
                             @NotBlank String timezone) {
}
