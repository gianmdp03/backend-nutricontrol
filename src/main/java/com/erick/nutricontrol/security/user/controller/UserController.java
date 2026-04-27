package com.erick.nutricontrol.security.user.controller;


import com.erick.nutricontrol.security.user.dto.user.UserDetailDTO;
import com.erick.nutricontrol.security.user.dto.user.UserUpdateDTO;
import com.erick.nutricontrol.security.user.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationService authService;

    @GetMapping("/me")
    public ResponseEntity<UserDetailDTO> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(authService.getUserByEmail(authentication.getName()));
    }

    @PatchMapping("/me")
    public ResponseEntity<String> updateMyProfile(
            @RequestBody UserUpdateDTO updateData,
            Authentication authentication
    ) {
        authService.updateUser(authentication.getName(), updateData);
        return ResponseEntity.ok("Perfil actualizado correctamente");
    }
}