package com.erick.nutricontrol.security.user.service;


import com.erick.nutricontrol.security.user.dto.authentication.AuthenticationRequestDTO;
import com.erick.nutricontrol.security.user.dto.authentication.AuthenticationResponseDTO;
import com.erick.nutricontrol.security.user.dto.user.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthenticationService {
    AuthenticationResponseDTO register(UserRequestDTO request);
    AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request);
    void forgotPassword(String email);
    String validateCode(String email, String code);
    boolean changeForgottenPassword(String email, String token, String password);
    boolean changePassword(String email, UserUpdatePassDTO dto);
    void updateUser(String email, UserUpdateDTO dto);
    Page<UserDetailDTO> listUsers(Pageable pageable);
    Page<UserDetailDTO> listBannedUsers(Pageable pageable);
    Page<UserDetailDTO> listAdmins(Pageable pageable);
    Page<UserDetailDTO> listPatients(Pageable pageable);
    Page<UserDetailDTO> searchUsersByUsername(String username, Pageable pageable);
    Page<UserDetailDTO> searchUsersByEmail(String email, Pageable pageable);
    UserDetailDTO getUserByEmail(String email);
    void promoteToAdmin(Long id);
    void toggleBan(Long id);
    UserDetailDTO getUserById(Long id);
    void updateProfilePicture(String username, UserProfilePictureDTO dto);
}
