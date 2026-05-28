package com.erick.nutricontrol.mapper;

import com.erick.nutricontrol.dto.adminPreset.AdminPresetDetailDTO;
import com.erick.nutricontrol.dto.adminPreset.AdminPresetRequestDTO;
import com.erick.nutricontrol.model.AdminPreset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class AdminPresetMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  public abstract AdminPreset toEntity(AdminPresetRequestDTO dto);

  public abstract AdminPresetDetailDTO toDetailDto(AdminPreset entity);
}
