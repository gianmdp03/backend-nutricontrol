package com.erick.nutricontrol.security.user.service.impl;

import com.erick.nutricontrol.exception.BadRequestException;
import com.erick.nutricontrol.exception.ConflictException;
import com.erick.nutricontrol.exception.NotFoundException;
import com.erick.nutricontrol.security.user.Enum.Role;
import com.erick.nutricontrol.security.user.dto.authentication.AuthenticationRequestDTO;
import com.erick.nutricontrol.security.user.dto.authentication.AuthenticationResponseDTO;
import com.erick.nutricontrol.security.user.dto.user.*;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.security.user.repository.UserRepository;
import com.erick.nutricontrol.security.user.service.AuthenticationService;
import com.erick.nutricontrol.security.user.service.JwtService;
import com.erick.nutricontrol.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;
  private final EmailService emailService;

  @Override
  @Transactional
  public AuthenticationResponseDTO register(UserRequestDTO request) {
    boolean emailAlreadyExists = userRepository.existsByEmail(request.email());
    boolean usernameAlreadyExists = userRepository.existsByUsername(request.username());
    if (emailAlreadyExists) {
      throw new ConflictException("Email is already in use");
    }
    if (usernameAlreadyExists) {
      throw new ConflictException("Username is already in use");
    }
    User user =
        new User(
            request.email(),
            passwordEncoder.encode(request.password()),
            request.name(),
            request.lastname(),
            Role.ROLE_PATIENT,
            request.username());
    userRepository.save(user);
    String jwtToken = jwtService.generateToken(user);
    UserDetailDTO userDetail =
        new UserDetailDTO(
            user.getId(),
            user.getName(),
            user.getLastname(),
            user.getEmail(),
            user.getUsername(),
            user.getRole().name(),
            user.isBanned(),
            user.isEmailConfirmed(),
            null);

    return new AuthenticationResponseDTO(jwtToken, userDetail);
  }

  @Override
  public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO request) {
      Authentication auth = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(request.loginInput(), request.password()));
      User user = (User) auth.getPrincipal();
      String jwtToken = jwtService.generateToken(user);
    UserDetailDTO userDetail =
        new UserDetailDTO(
            user.getId(),
            user.getName(),
            user.getLastname(),
            user.getEmail(),
            user.getUsername(),
            user.getRole().name(),
            user.isBanned(),
            user.isEmailConfirmed(),
            null);

    return new AuthenticationResponseDTO(jwtToken, userDetail);
  }

  @Override
  @Transactional
  public void forgotPassword(String email) {
    Optional<User> existingUser = userRepository.findByEmail(email);
    if (existingUser.isPresent()) {
      User user = existingUser.get();
      SecureRandom secureRandom = new SecureRandom();
      String token = String.valueOf(100000 + secureRandom.nextInt(999999));
      user.setSecurityToken(token);
      user.setTokenExpirationTime(LocalDateTime.now().plusMinutes(20));
      String body =
          "Estimado usuario "
              + user.getName()
              + " "
              + user.getLastname()
              + "\n\n"
              + "Aquí tiene el código de verificación para cambiar su contraseña: "
              + user.getSecurityToken()
              + "\n\n"
              + "El mismo estará disponible por 20 minutos";
      emailService.sendEmail(email, "Cambiar contraseña", body);
    }
  }

  @Override
  @Transactional
  public String validateCode(String email, String code) {
    Optional<User> existingUser = userRepository.findByEmail(email);
    if (existingUser.isPresent()) {
      User user = existingUser.get();
      if (user.getSecurityToken().equals(code)
          && user.getTokenExpirationTime().isAfter(LocalDateTime.now())) {
        String token = UUID.randomUUID().toString();
        user.setSecurityToken(token);
        return token;
      }
    }
    return null;
  }

  @Override
  @Transactional
  public boolean changeForgottenPassword(String email, String token, String password) {
    Optional<User> existingUser = userRepository.findByEmail(email);
    if (existingUser.isPresent()) {
      User user = existingUser.get();
      if (user.getSecurityToken().equals(token)) {
        user.setPassword(passwordEncoder.encode(password));
        user.setSecurityToken(null);
        user.setTokenExpirationTime(null);
        return true;
      }
    }
    return false;
  }

  @Override
  @Transactional
  public boolean changePassword(String email, UserUpdatePassDTO dto) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new BadRequestException("Invalid request"));
    if (passwordEncoder.matches(dto.currentPassword(), user.getPassword())) {
      user.setPassword(passwordEncoder.encode(dto.newPassword()));
      return true;
    }
    return false;
  }

  @Override
  @Transactional
  public void updateUser(String username, UserUpdateDTO dto) {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new BadRequestException("Invalid request"));
    if (dto.name() != null) {
      user.setName(dto.name());
    }
    if (dto.lastname() != null) {
      user.setLastname(dto.lastname());
    }
    if (dto.email() != null) {
      user.setEmail(dto.email());
    }
  }

  @Override
  public Page<UserDetailDTO> listUsers(Pageable pageable) {
    Page<User> page = userRepository.findByIsBannedFalse(pageable);
    if (page.isEmpty()) {
      return Page.empty();
    }
    return page.map(
        user ->
            new UserDetailDTO(
                user.getId(),
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                user.getUsername(),
                user.getRole().name(),
                user.isBanned(),
                user.isEmailConfirmed(),
                user.getProfilePicture()));
  }

  @Override
  public Page<UserDetailDTO> listBannedUsers(Pageable pageable) {
    Page<User> page = userRepository.findByIsBannedTrue(pageable);
    if (page.isEmpty()) {
      return Page.empty();
    }
    return page.map(
        user ->
            new UserDetailDTO(
                user.getId(),
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                user.getUsername(),
                user.getRole().name(),
                user.isBanned(),
                user.isEmailConfirmed(),
                user.getProfilePicture()));
  }

  @Override
  public Page<UserDetailDTO> listAdmins(Pageable pageable){
      Page<User> page = userRepository.findByRoleAndIsBannedFalse(Role.ROLE_ADMIN, pageable);
      if(page.isEmpty()) {
          return Page.empty();
      }
      return page.map(user -> new UserDetailDTO(
              user.getId(),
              user.getName(),
              user.getLastname(),
              user.getEmail(),
              user.getUsername(),
              user.getRole().name(),
              user.isBanned(),
              user.isEmailConfirmed(),
              user.getProfilePicture()));
  }

  @Override
  public Page<UserDetailDTO> listPatients(Pageable pageable) {
    Page<User> page = userRepository.findByRoleAndIsBannedFalse(Role.ROLE_PATIENT, pageable);
    if (page.isEmpty()) {
      return Page.empty();
    }
    return page.map(
        user ->
            new UserDetailDTO(
                user.getId(),
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                user.getUsername(),
                user.getRole().name(),
                user.isBanned(),
                user.isEmailConfirmed(),
                user.getProfilePicture()));
  }

  @Override
  public Page<UserDetailDTO> searchUsersByUsername(String username, Pageable pageable) {
    Page<User> page = userRepository.findByUsernameContainingIgnoreCase(username, pageable);
    if (page.isEmpty()) {
      return Page.empty();
    }
    return page.map(
        user ->
            new UserDetailDTO(
                user.getId(),
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                user.getUsername(),
                user.getRole().name(),
                user.isBanned(),
                user.isEmailConfirmed(),
                user.getProfilePicture()));
  }

  @Override
  public Page<UserDetailDTO> searchUsersByEmail(String email, Pageable pageable) {
    Page<User> page = userRepository.findByEmailContainingIgnoreCase(email, pageable);
    if (page.isEmpty()) {
      return Page.empty();
    }
    return page.map(
        user ->
            new UserDetailDTO(
                user.getId(),
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                user.getUsername(),
                user.getRole().name(),
                user.isBanned(),
                user.isEmailConfirmed(),
                user.getProfilePicture()));
  }

  @Override
  public UserDetailDTO getUserByEmail(String email) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new NotFoundException("Email is not valid"));
    return new UserDetailDTO(
        user.getId(),
        user.getName(),
        user.getLastname(),
        user.getEmail(),
        user.getUsername(),
        user.getRole().name(),
        user.isBanned(),
        user.isEmailConfirmed(),
        user.getProfilePicture());
  }

  @Override
  @Transactional
  public void promoteToAdmin(Long id) {
    User user =
        userRepository.findById(id).orElseThrow(() -> new BadRequestException("Invalid request"));
    user.setRole(Role.ROLE_ADMIN);
  }

  @Override
  @Transactional
  public void toggleBan(Long id) {
    User user =
        userRepository.findById(id).orElseThrow(() -> new BadRequestException("Invalid request"));
    user.setBanned(!user.isBanned());
  }

  @Override
  public UserDetailDTO getUserById(Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

    return new UserDetailDTO(
        user.getId(),
        user.getName(),
        user.getLastname(),
        user.getEmail(),
        user.getUsername(),
        user.getRole().name(),
        user.isBanned(),
        user.isEmailConfirmed(),
        user.getProfilePicture());
  }

  @Override
    @Transactional
    public void updateProfilePicture(String username, UserProfilePictureDTO dto){
      User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Username does not exist"));
      user.setProfilePicture(dto.profilePicture());
  }
}
