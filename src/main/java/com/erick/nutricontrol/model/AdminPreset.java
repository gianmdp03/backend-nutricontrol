package com.erick.nutricontrol.model;

import com.erick.nutricontrol.security.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "admin_presets")
public class AdminPreset {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String adminName;

  @Column(nullable = false)
  private String specialty;

  @Column(nullable = false)
  private String exequatur;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  public AdminPreset(String adminName, String specialty, String exequatur, User user) {
    this.adminName = adminName;
    this.specialty = specialty;
    this.exequatur = exequatur;
    this.user = user;

    if (user != null) {
      user.setAdminPreset(this);
    }
  }
}
