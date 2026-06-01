package com.erick.nutricontrol.dto.nutritionalPlan;

import com.erick.nutricontrol.extra.DailyMenu;
import java.time.DayOfWeek;
import java.util.Map;

public record NutritionalPlanDetailDTO(
    Long id,
    String patientName,
    String age,
    Map<DayOfWeek, DailyMenu> weeklyMenu,
    String adminName,
    String adminSpecialty,
    String textareaTexto,
    String date) {}
