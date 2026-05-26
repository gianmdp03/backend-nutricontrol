package com.erick.nutricontrol.mapper;

import com.erick.nutricontrol.dto.appointment.AppointmentDetailDTO;
import com.erick.nutricontrol.dto.appointment.AppointmentRequestDTO;
import com.erick.nutricontrol.model.Appointment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class AppointmentMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "date", ignore = true)
  @Mapping(target = "startTime", ignore = true)
  @Mapping(target = "endTime", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "admin", ignore = true)
  public abstract Appointment toEntity(AppointmentRequestDTO dto);

  public abstract AppointmentDetailDTO toDetailDTO(Appointment entity);
}
