package com.erick.nutricontrol.mapper;


import com.erick.nutricontrol.dto.service.ServiceDetailDTO;
import com.erick.nutricontrol.dto.service.ServiceRequestDTO;
import com.erick.nutricontrol.dto.service.ServiceUpdateDTO;
import com.erick.nutricontrol.model.Service;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class ServiceMapper {
  @Mapping(target = "id", ignore = true)
  public abstract Service toEntity(ServiceRequestDTO dto);

  public abstract ServiceDetailDTO toDetailDTO(Service entity);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "id", ignore = true)
  public abstract void updateEntityFromDto(
          ServiceUpdateDTO dto, @MappingTarget Service entity);
}
