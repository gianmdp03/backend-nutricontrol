package com.erick.nutricontrol.security.user.dto.authentication;

import com.erick.nutricontrol.security.user.dto.user.UserDetailDTO;

public record AuthenticationResponseDTO(String token, UserDetailDTO dto) {
}
