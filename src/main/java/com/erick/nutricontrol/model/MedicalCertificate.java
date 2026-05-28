package com.erick.nutricontrol.model;

import com.erick.nutricontrol.security.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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

    @Column(nullable = false)
    private String adminName;

    @Column(nullable = false)
    private String specialty;

    @Column(nullable = false)
    private String exequatur;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public MedicalCertificate(String patientName, String age, String adminName, String specialty, String exequatur, LocalDate date, User user) {
        this.patientName = patientName;
        this.age = age;
        this.adminName = adminName;
        this.specialty = specialty;
        this.exequatur = exequatur;
        this.date = date;
        this.user = user;
    }
}
