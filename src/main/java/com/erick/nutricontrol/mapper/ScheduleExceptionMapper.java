package com.erick.nutricontrol.mapper;


import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionDetailDTO;
import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionRequestDTO;
import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionUpdateDTO;
import com.erick.nutricontrol.model.ScheduleException;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class ScheduleExceptionMapper {
    @Mapping(target = "id", ignore = true)
    public abstract ScheduleException toEntity(ScheduleExceptionRequestDTO dto);
    public abstract ScheduleExceptionDetailDTO toDetailDTO(ScheduleException entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    public abstract void updateEntityFromDto(ScheduleExceptionUpdateDTO dto, @MappingTarget ScheduleException entity);
}
