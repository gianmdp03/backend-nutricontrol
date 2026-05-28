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

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  public AdminPreset(String adminName, String specialty, String exequatur) {
    this.adminName = adminName;
    this.specialty = specialty;
    this.exequatur = exequatur;
  }
}
