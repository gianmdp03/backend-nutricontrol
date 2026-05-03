package com.erick.nutricontrol.model;

import com.erick.nutricontrol.security.user.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "schedule_exceptions")
@Getter
@Setter
@NoArgsConstructor
public class ScheduleException {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDate date;

  @Column(nullable = false)
  private LocalTime startTime;

  @Column(nullable = false)
  private LocalTime endTime;

  private String reason;

  @ManyToOne
  @JoinColumn(name = "admin_id")
  private User admin;

  public ScheduleException(LocalDate date, LocalTime startTime, LocalTime endTime, String reason, User admin) {
    this.date = date;
    this.startTime = startTime;
    this.endTime = endTime;
    this.reason = reason;
    this.admin = admin;
  }
}
