package com.erick.nutricontrol.security.user.dto.user;

import jakarta.validation.constraints.Email;

public record UserUpdateDTO (String name, String lastname, String username, @Email String email, String role){
}
