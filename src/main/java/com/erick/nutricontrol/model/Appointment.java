package com.erick.nutricontrol.model;

import com.erick.nutricontrol._enum.AppointmentStatus;
import com.erick.nutricontrol._enum.AppointmentType;
import com.erick.nutricontrol.security.user.model.User;
import jakarta.persistence.*;
import java.time.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
    name = "appointments",
    indexes = {
      @Index(name = "idx_appointment_user", columnList = "user_id"),
      @Index(name = "idx_appointment_admin", columnList = "admin_id"),
      @Index(name = "idx_appointment_status", columnList = "appointmentStatus"),
      @Index(name = "idx_appointment_admin_date", columnList = "admin_id, date"),
      @Index(name = "idx_appointment_start_utc", columnList = "start_time_utc"),
      @Index(
          name = "idx_appointment_reminders",
          columnList = "reminder_24h_sent, reminder_15m_sent")
    })
public class Appointment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDate date;

  @Column(nullable = false)
  private LocalTime startTime;

  @Column(nullable = false)
  private LocalTime endTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "admin_id", nullable = false)
  private User admin;

  @OneToMany(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Payment> payments = new HashSet<>();

  @Column(length = 20, nullable = false)
  @Enumerated(EnumType.STRING)
  private AppointmentStatus appointmentStatus;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private AppointmentType appointmentType;

  @Column(name = "meeting_link")
  private String meetingLink;

  @Column(name = "start_time_utc", nullable = false)
  private OffsetDateTime startTimeUtc;

  @Column(name = "end_time_utc", nullable = false)
  private OffsetDateTime endTimeUtc;

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @Column(name = "reminder_24h_sent", nullable = false)
  private boolean reminder24hSent = false;

  @Column(name = "reminder_15m_sent", nullable = false)
  private boolean reminder15mSent = false;

  public Appointment(
      LocalDate date,
      LocalTime startTime,
      LocalTime endTime,
      AppointmentType appointmentType,
      User user,
      User admin,
      AppointmentStatus appointmentStatus) {
    this.date = date;
    this.startTime = startTime;
    this.endTime = endTime;
    this.appointmentType = appointmentType;
    this.user = user;
    this.admin = admin;
    this.appointmentStatus = appointmentStatus;
  }

  @PrePersist
  protected void onCreate() {
    this.createdAt = OffsetDateTime.now(ZoneOffset.UTC);
  }
}
