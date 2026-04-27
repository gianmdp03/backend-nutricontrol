package com.erick.nutricontrol.security.user.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserUpdatePassDTO (@NotBlank String currentPassword, @NotBlank String newPassword){
}
