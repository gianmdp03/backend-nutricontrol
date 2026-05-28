package com.erick.nutricontrol.model;

import com.erick.nutricontrol.security.user.model.User;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "medical_certificates")
public class MedicalCertificate {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String patientName;

  @Column(nullable = false)
  private String age;

  @Column(columnDefinition = "TEXT")
  private String textareaTexto;

  @Column(nullable = false)
  private String adminName;

  @Column(nullable = false)
  private String specialty;

  @Column(nullable = false)
  private String exequatur;

  @Column(nullable = false)
  private OffsetDateTime dateTime = OffsetDateTime.now(ZoneOffset.UTC);

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public MedicalCertificate(String patientName, String age, String textareaTexto, User user) {
    this.patientName = patientName;
    this.age = age;
    this.textareaTexto = textareaTexto;
    this.user = user;
  }
}
