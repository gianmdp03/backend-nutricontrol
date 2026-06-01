package com.erick.nutricontrol.model;

import com.erick.nutricontrol.extra.DailyMenu;
import com.erick.nutricontrol.security.user.model.User;
import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.EnumMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "nutritional_plans")
@NoArgsConstructor
@Getter
@Setter
public class NutritionalPlan {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String patientName;

  @Column(nullable = false)
  private String age;

  @Column(nullable = false)
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<DayOfWeek, DailyMenu> weeklyMenu = new EnumMap<>(DayOfWeek.class);

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(nullable = false)
  private String adminName;

  @Column(nullable = false)
  private String adminSpecialty;

  @Column(nullable = false)
  private String textareaTexto;

  @Column(nullable = false)
  private OffsetDateTime dateTime = OffsetDateTime.now(ZoneOffset.UTC);

  public NutritionalPlan(
      String patientName,
      String age,
      Map<DayOfWeek, DailyMenu> weeklyMenu,
      User user,
      String adminName,
      String adminSpecialty,
      String textareaTexto) {
    this.patientName = patientName;
    this.age = age;
    this.weeklyMenu = weeklyMenu;
    this.user = user;
    this.adminName = adminName;
    this.adminSpecialty = adminSpecialty;
    this.textareaTexto = textareaTexto;
  }
}
