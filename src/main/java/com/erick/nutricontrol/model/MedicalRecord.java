package com.erick.nutricontrol.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "medical_records")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double weight;

    @Column(nullable = false)
    private Double height;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String medicalHistory;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String medication;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime creationDate;

    public MedicalRecord(Double weight, Double height, String medicalHistory, String medication) {
        this.weight = weight;
        this.height = height;
        this.medicalHistory = medicalHistory;
        this.medication = medication;
    }
}
