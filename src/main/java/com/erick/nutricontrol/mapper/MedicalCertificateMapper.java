package com.erick.nutricontrol.mapper;

import com.erick.nutricontrol.dto.medicalCertificate.MedicalCertificateDetailDTO;
import com.erick.nutricontrol.dto.medicalCertificate.MedicalCertificateRequestDTO;
import com.erick.nutricontrol.model.MedicalCertificate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class MedicalCertificateMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "adminName", ignore = true)
  @Mapping(target = "specialty", ignore = true)
  @Mapping(target = "exequatur", ignore = true)
  @Mapping(target = "dateTime", ignore = true)
  public abstract MedicalCertificate toEntity(MedicalCertificateRequestDTO dto);

  @Mapping(target = "date", source = "formattedDate")
  public abstract MedicalCertificateDetailDTO toDetailDto(
      MedicalCertificate entity, String formattedDate);
}
