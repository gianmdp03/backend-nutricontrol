package com.erick.nutricontrol.mapper;

import com.erick.nutricontrol.dto.medicalHistory.MedicalHistoryDetailDTO;
import com.erick.nutricontrol.dto.medicalHistory.MedicalHistoryRequestDTO;
import com.erick.nutricontrol.model.MedicalHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = MedicalHistoryTrackingMapper.class)
public abstract class MedicalHistoryMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "trackings", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "admin", ignore = true)
  public abstract MedicalHistory toEntity(MedicalHistoryRequestDTO dto);

  public abstract MedicalHistoryDetailDTO toDetailDTO(MedicalHistory entity);
}
