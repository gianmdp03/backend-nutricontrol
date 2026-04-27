package com.erick.nutricontrol.security.user.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationPasswordDTO(@NotBlank @Email String email, String password, String token){}
