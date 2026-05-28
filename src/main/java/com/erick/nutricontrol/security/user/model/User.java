package com.erick.nutricontrol.security.user.model;

import com.erick.nutricontrol.model.AdminPreset;
import com.erick.nutricontrol.security.user.Enum.Role;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "_user")
@Getter
@Setter
@NoArgsConstructor
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String lastname;

  private String profilePicture;

  @Column(nullable = false)
  private boolean isEmailConfirmed = false;

  @Column(name = "timezone", nullable = false)
  private String timezone;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", length = 20, nullable = false)
  private Role role;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private AdminPreset adminPreset;

  private int reviewCount = 0;

  private int totalScore = 0;

  private double averageRating = 0.0;

  private String securityToken;

  private LocalDateTime tokenExpirationTime;

  private boolean isBanned = false;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.name()));
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !isBanned;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Builder
  public User(
      String email,
      String password,
      String name,
      String lastname,
      Role role,
      String username,
      String timezone) {
    this.email = email;
    this.password = password;
    this.name = name;
    this.lastname = lastname;
    this.role = role;
    this.username = username;
    this.timezone = timezone;
  }
}
