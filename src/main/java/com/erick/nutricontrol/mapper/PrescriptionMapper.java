package com.erick.nutricontrol.mapper;

import com.erick.nutricontrol.dto.prescription.PrescriptionDetailDTO;
import com.erick.nutricontrol.dto.prescription.PrescriptionRequestDTO;
import com.erick.nutricontrol.model.Prescription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class PrescriptionMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "adminName", ignore = true)
  @Mapping(target = "specialty", ignore = true)
  @Mapping(target = "exequatur", ignore = true)
  @Mapping(target = "dateTime", ignore = true)
  public abstract Prescription toEntity(PrescriptionRequestDTO dto);

  @Mapping(target = "date", source = "formattedDate")
  public abstract PrescriptionDetailDTO toDetailDto(Prescription entity, String formattedDate);
}
