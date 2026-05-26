package com.erick.nutricontrol.security.user.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateDTO(
    @Size(max = 50) String name,
    @Size(max = 50) String lastname,
    @Size(max = 50) String username,
    @Email @Size(max = 100) String email,
    @Size(max = 50) String role) {}
