package com.erick.nutricontrol.security.user.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserProfilePictureDTO (@NotBlank String profilePicture){}
