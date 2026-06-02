package com.erick.nutricontrol.dto.nutritionalPlan;

import com.erick.nutricontrol.extra.DailyMenu;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.time.DayOfWeek;
import java.util.Map;

public record NutritionalPlanRequestDTO(
    @NotBlank @Size(max = 100) String patientName,
    @NotBlank @Size(max = 3) String age,
    @NotEmpty @Size(min = 7, max = 7) Map<DayOfWeek, DailyMenu> weeklyMenu,
    @NotBlank @Size(max = 2000) String textareaTexto,
    Long userId) {}
