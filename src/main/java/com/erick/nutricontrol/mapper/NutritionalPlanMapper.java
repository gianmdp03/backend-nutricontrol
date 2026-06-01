package com.erick.nutricontrol.mapper;

import com.erick.nutricontrol.dto.nutritionalPlan.NutritionalPlanDetailDTO;
import com.erick.nutricontrol.dto.nutritionalPlan.NutritionalPlanRequestDTO;
import com.erick.nutricontrol.model.NutritionalPlan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class NutritionalPlanMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "adminName", ignore = true)
  @Mapping(target = "adminSpecialty", ignore = true)
  @Mapping(target = "user", ignore = true)
  public abstract NutritionalPlan toEntity(NutritionalPlanRequestDTO dto);

  @Mapping(target = "date", source = "formattedDate")
  public abstract NutritionalPlanDetailDTO toDetailDto(
      NutritionalPlan entity, String formattedDate);
}
