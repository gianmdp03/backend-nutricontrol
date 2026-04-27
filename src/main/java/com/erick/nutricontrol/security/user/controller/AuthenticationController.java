package com.erick.nutricontrol.security.user.controller;


import com.erick.nutricontrol.security.user.dto.authentication.AuthenticationPasswordDTO;
import com.erick.nutricontrol.security.user.dto.authentication.AuthenticationRequestDTO;
import com.erick.nutricontrol.security.user.dto.authentication.AuthenticationResponseDTO;
import com.erick.nutricontrol.security.user.dto.user.*;
import com.erick.nutricontrol.security.user.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<UserDetailDTO> register(@Valid @RequestBody UserRequestDTO request) {
        AuthenticationResponseDTO authResponse = authenticationService.register(request);
        ResponseCookie cookie = createAccessTokenCookie(authResponse.token());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(authResponse.dto());
    }

    @PostMapping("/login")
    public ResponseEntity<UserDetailDTO> authenticate(@Valid @RequestBody AuthenticationRequestDTO request) {
        AuthenticationResponseDTO authResponse = authenticationService.authenticate(request);
        ResponseCookie cookie = createAccessTokenCookie(authResponse.token());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(authResponse.dto());
    }

    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody AuthenticationPasswordDTO dto){
        authenticationService.forgotPassword(dto.email());
        return ResponseEntity.status(HttpStatus.OK).body("Si el email es correcto, se envió un código de verificación");
    }

    @PostMapping("/verify/{code}")
    public ResponseEntity<String> validateCode(@Valid @RequestBody AuthenticationPasswordDTO dto, @PathVariable String code){
        String token = authenticationService.validateCode(dto.email(), code);
        if(token == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verification code is not valid");
        }
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @PatchMapping("/forgot/change")
    public ResponseEntity<Void> changeForgottenPassword(@Valid @RequestBody AuthenticationPasswordDTO dto){
        boolean flag = authenticationService.changeForgottenPassword(dto.email(), dto.token(), dto.password());
        if(!flag){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/logged/password")
    public ResponseEntity<Void> changePassword(Authentication authentication, @Valid @RequestBody UserUpdatePassDTO dto){
        boolean flag = authenticationService.changePassword(authentication.getName(), dto);
        if(!flag){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/logged/user")
    public ResponseEntity<Void> updateUser(Authentication authentication, @Valid @RequestBody UserUpdateDTO dto){
        authenticationService.updateUser(authentication.getName(), dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/admin")
    public ResponseEntity<Page<UserDetailDTO>> listUsers(
            @PageableDefault(page=0, size = 10, sort="name", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.listUsers(pageable));
    }

    @GetMapping("/admin/banned")
    public ResponseEntity<Page<UserDetailDTO>> listBannedUsers(
            @PageableDefault(page=0, size = 10, sort="name", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.listBannedUsers(pageable));
    }

    @GetMapping("/admins")
    public ResponseEntity<Page<UserDetailDTO>> listAdmins(@PageableDefault(page=0, size = 10, sort="name", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.listAdmins(pageable));
    }

    @GetMapping("/admin/patients")
    public ResponseEntity<Page<UserDetailDTO>> listPatients(
            @PageableDefault(page=0, size = 10, sort="name", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.listPatients(pageable));
    }

    @GetMapping("/admin/user/username/{username}")
    public ResponseEntity<Page<UserDetailDTO>> searchUsersByUsername(@PathVariable String username, @PageableDefault(page=0, size = 10, sort="name", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.searchUsersByUsername(username, pageable));
    }

    @GetMapping("/admin/user/email/{email}")
    public ResponseEntity<Page<UserDetailDTO>> searchUsersByEmail(@PathVariable String email,
                                                                  @PageableDefault(page=0, size = 10, sort="name", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.searchUsersByEmail(email, pageable));
    }

    @GetMapping("/admin/user/search")
    public ResponseEntity<UserDetailDTO> getUserByEmail(@RequestParam String email){
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.getUserByEmail(email));
    }

    @PatchMapping("/admin/promote/admin/{id}")
    public ResponseEntity<Void> promoteToAdmin(@PathVariable Long id){
        authenticationService.promoteToAdmin(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/admin/ban/{id}")
    public ResponseEntity<Void> toggleBan(@PathVariable Long id){
        authenticationService.toggleBan(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/admin/user-by-id/{id}")
    public ResponseEntity<UserDetailDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(authenticationService.getUserById(id));
    }

    @PatchMapping("/logged/picture")
    public ResponseEntity<Void> updateProfilePicture(Authentication authentication, @Valid @RequestBody UserProfilePictureDTO dto){
        authenticationService.updateProfilePicture(authentication.getName(), dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/logged/profile")
    public ResponseEntity<UserDetailDTO> getProfile(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(authenticationService.getUserByEmail(authentication.getName()));
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    private ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from("accessToken", token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Strict")
                .build();
    }
}
