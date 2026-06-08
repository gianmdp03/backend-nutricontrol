package com.erick.nutricontrol.mapper;

import com.erick.nutricontrol.dto.medicalHistoryTracking.MedicalHistoryTrackingDetailDTO;
import com.erick.nutricontrol.dto.medicalHistoryTracking.MedicalHistoryTrackingRequestDTO;
import com.erick.nutricontrol.model.MedicalHistoryTracking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class MedicalHistoryTrackingMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "medicalHistory", ignore = true)
  @Mapping(target = "datetime", ignore = true)
  public abstract MedicalHistoryTracking toEntity(MedicalHistoryTrackingRequestDTO dto);

  @Mapping(target = "datetime", source = "formattedDate")
  public abstract MedicalHistoryTrackingDetailDTO toDetailDTO(
      MedicalHistoryTracking entity, String formattedDate);
}
