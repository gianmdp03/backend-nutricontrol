package com.erick.nutricontrol.mapper;

import com.erick.nutricontrol.dto.medicalRecord.MedicalRecordDetailDTO;
import com.erick.nutricontrol.dto.medicalRecord.MedicalRecordRequestDTO;
import com.erick.nutricontrol.model.MedicalRecord;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class MedicalRecordMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  public abstract MedicalRecord toEntity(MedicalRecordRequestDTO dto);

  public abstract MedicalRecordDetailDTO toDetailDTO(MedicalRecord entity);
}
