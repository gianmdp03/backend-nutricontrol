package com.erick.nutricontrol.mapper;


import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleDetailDTO;
import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleRequestDTO;
import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleUpdateDTO;
import com.erick.nutricontrol.model.ScheduleRule;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class ScheduleRuleMapper {
    @Mapping(target = "id", ignore = true)
    public abstract ScheduleRule toEntity(ScheduleRuleRequestDTO dto);
    public abstract ScheduleRuleDetailDTO toDetailDTO(ScheduleRule entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    public abstract void updateEntityFromDto(ScheduleRuleUpdateDTO dto, @MappingTarget ScheduleRule entity);
}
