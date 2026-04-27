package com.erick.nutricontrol.mapper;

import com.erick.nutricontrol.dto.appointment.AppointmentDetailDTO;
import com.erick.nutricontrol.dto.appointment.AppointmentRequestDTO;
import com.erick.nutricontrol.dto.appointment.AppointmentUpdateDTO;
import com.erick.nutricontrol.model.Appointment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class AppointmentMapper {
    @Mapping(target = "id", ignore = true)
    public abstract Appointment toEntity(AppointmentRequestDTO dto);
    public abstract AppointmentDetailDTO toDetailDTO(Appointment entity);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    public abstract void updateEntityFromDto(AppointmentUpdateDTO dto,  @MappingTarget Appointment entity);

}
