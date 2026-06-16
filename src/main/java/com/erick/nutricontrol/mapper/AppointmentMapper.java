package com.erick.nutricontrol.mapper;

import com.erick.nutricontrol.dto.appointment.AppointmentDetailDTO;
import com.erick.nutricontrol.dto.appointment.AppointmentRequestDTO;
import com.erick.nutricontrol.model.Appointment;
import com.erick.nutricontrol.repository.ReviewRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class AppointmentMapper {
  @Autowired
  protected ReviewRepository reviewRepository;

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "date", ignore = true)
  @Mapping(target = "startTime", ignore = true)
  @Mapping(target = "endTime", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(target = "admin", ignore = true)
  public abstract Appointment toEntity(AppointmentRequestDTO dto);

  @Mapping(target = "hasReviewed", expression = "java(entity.getId() != null && reviewRepository.existsByAppointmentId(entity.getId()))")
  public abstract AppointmentDetailDTO toDetailDTO(Appointment entity);
}
