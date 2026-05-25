package com.erick.nutricontrol.model;

import com.erick.nutricontrol.security.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @Column(nullable = false)
    private int score;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  //Usuario que deja la review

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin; //Admin que recibe la review

    @Column(nullable = false, updatable = false)
    private OffsetDateTime date = OffsetDateTime.now(ZoneOffset.UTC);

    public Review(Appointment appointment, int score, String comment, User user, User admin) {
        this.appointment = appointment;
        this.score = score;
        this.comment = comment;
        this.user = user;
        this.admin = admin;
    }
}
