package com.erick.nutricontrol.model;

import com.erick.nutricontrol.extra.*;
import com.erick.nutricontrol.security.user.model.User;
import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "medical_histories")
@Getter
@Setter
@NoArgsConstructor
public class MedicalHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JdbcTypeCode(SqlTypes.JSON)
  private PatientData patientData;

  @OneToMany(mappedBy = "medicalHistory")
  @OrderBy("datetime DESC")
  private Set<MedicalHistoryTracking> trackings = new LinkedHashSet<>();

  @Column(columnDefinition = "TEXT")
  private String allergies;

  @Column(columnDefinition = "TEXT")
  private String currentIllnessHistory;

  @JdbcTypeCode(SqlTypes.JSON)
  private ToxicHabits toxicHabits;

  @JdbcTypeCode(SqlTypes.JSON)
  private FamilyHistory familyHistory;

  @JdbcTypeCode(SqlTypes.JSON)
  private SystemReview systemReview;

  @JdbcTypeCode(SqlTypes.JSON)
  private VitalSigns vitalSigns;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "admin_id")
  private User admin;

  @Builder
  public MedicalHistory(
      PatientData patientData,
      String allergies,
      String currentIllnessHistory,
      ToxicHabits toxicHabits,
      FamilyHistory familyHistory,
      SystemReview systemReview,
      VitalSigns vitalSigns,
      User user,
      User admin) {
    this.patientData = patientData;
    this.allergies = allergies;
    this.currentIllnessHistory = currentIllnessHistory;
    this.toxicHabits = toxicHabits;
    this.familyHistory = familyHistory;
    this.systemReview = systemReview;
    this.vitalSigns = vitalSigns;
    this.user = user;
    this.admin = admin;
  }
}
