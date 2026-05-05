package com.erick.nutricontrol.security.user.dto.user;

public record UserDetailDTO(
    Long id,
    String name,
    String lastname,
    String email,
    String username,
    String role,
    String timezone,
    boolean banned,
    boolean isEmailConfirmed,
    String profilePicture) {}
