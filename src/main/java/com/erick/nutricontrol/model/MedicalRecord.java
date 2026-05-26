package com.erick.nutricontrol.model;

import com.erick.nutricontrol.security.user.model.User;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime creationDate;

    public MedicalRecord(Double weight, Double height, String medicalHistory, String medication, User user) {
        this.weight = weight;
        this.height = height;
        this.medicalHistory = medicalHistory;
        this.medication = medication;
        this.user = user;
    }
}