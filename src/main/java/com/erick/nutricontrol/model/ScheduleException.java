package com.erick.nutricontrol.model;

import com.erick.nutricontrol.security.user.model.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "schedule_exceptions", indexes = {
        @Index(name = "idx_schedule_exception_admin_date", columnList = "admin_id, date")
})
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

  @ManyToOne(fetch = FetchType.LAZY)
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
