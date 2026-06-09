package com.erick.nutricontrol.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "medical_history_trackings")
public class MedicalHistoryTracking {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String consultationReason;

  @Column(columnDefinition = "TEXT")
  private String labResultsAndImages;

  @Column(columnDefinition = "TEXT")
  private String diagnosticImpression;

  @Column(columnDefinition = "TEXT")
  private String medicalPlan;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "medical_history_id", nullable = false)
  private MedicalHistory medicalHistory;

  @Column(nullable = false, updatable = false)
  private OffsetDateTime datetime = OffsetDateTime.now(ZoneOffset.UTC);

  @Builder
  public MedicalHistoryTracking(
      String consultationReason,
      String labResultsAndImages,
      String diagnosticImpression,
      String medicalPlan,
      MedicalHistory medicalHistory) {
    this.consultationReason = consultationReason;
    this.labResultsAndImages = labResultsAndImages;
    this.diagnosticImpression = diagnosticImpression;
    this.medicalPlan = medicalPlan;
    this.medicalHistory = medicalHistory;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MedicalHistoryTracking that)) return false;
    return id != null && id.equals(that.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
