package com.erick.nutricontrol.mapper;

import com.erick.nutricontrol.dto.nutritionalPlan.NutritionalPlanDetailDTO;
import com.erick.nutricontrol.dto.nutritionalPlan.NutritionalPlanRequestDTO;
import com.erick.nutricontrol.model.NutritionalPlan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class NutritionalPlanMapper {
  public abstract NutritionalPlan toEntity(NutritionalPlanRequestDTO dto);

  public abstract NutritionalPlanDetailDTO toDetailDto(NutritionalPlan entity);
}
