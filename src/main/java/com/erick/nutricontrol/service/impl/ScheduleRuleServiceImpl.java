package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleDetailDTO;
import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleRequestDTO;
import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleUpdateDTO;
import com.erick.nutricontrol.exception.ConflictException;
import com.erick.nutricontrol.exception.NotFoundException;
import com.erick.nutricontrol.mapper.ScheduleRuleMapper;
import com.erick.nutricontrol.model.ScheduleRule;
import com.erick.nutricontrol.repository.ScheduleRuleRepository;
import com.erick.nutricontrol.service.ScheduleRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleRuleServiceImpl implements ScheduleRuleService {
  private final ScheduleRuleRepository repository;
  private final ScheduleRuleMapper mapper;

  @Override
  @Transactional
  public ScheduleRuleDetailDTO addScheduleRule(ScheduleRuleRequestDTO dto) {
    List<ScheduleRule> list = repository.findByDayOfWeek(dto.dayOfWeek());
    LocalTime newStart = dto.startTime();
    LocalTime newEnd = dto.endTime();
    for (ScheduleRule existing : list) {
      boolean overlaps =
          newStart.isBefore(existing.getStartTime()) && newEnd.isAfter(existing.getEndTime());
      if (overlaps) {
        throw new ConflictException("Schedule Rule already exists");
      }
    }
    ScheduleRule scheduleRule = mapper.toEntity(dto);
    scheduleRule = repository.save(scheduleRule);
    return mapper.toDetailDTO(scheduleRule);
  }

  @Override
  public Page<ScheduleRuleDetailDTO> listScheduleRule(Pageable pageable) {
    Page<ScheduleRule> list = repository.findAll(pageable);
    if (list.isEmpty()) {
      return Page.empty();
    }
    return list.map(mapper::toDetailDTO);
  }

  @Override
  public ScheduleRuleDetailDTO getScheduleRuleById(Long id) {
    ScheduleRule scheduleRule = repository.findById(id).orElseThrow(() -> new NotFoundException("Schedule Rule Not Found"));
    return mapper.toDetailDTO(scheduleRule);
  }

  @Override
  @Transactional
  public ScheduleRuleDetailDTO updateScheduleRule(Long id, ScheduleRuleUpdateDTO dto){
    ScheduleRule scheduleRule = repository.findById(id).orElseThrow(() -> new NotFoundException("Schedule Rule Not Found"));
    mapper.updateEntityFromDto(dto, scheduleRule);
    scheduleRule = repository.save(scheduleRule);
    return mapper.toDetailDTO(scheduleRule);
  }

  @Override
  @Transactional
  public void deleteScheduleRuleById(Long id) {
    ScheduleRule scheduleRule = repository.findById(id).orElseThrow(() -> new NotFoundException("Schedule Rule Not Found"));
    repository.delete(scheduleRule);
  }
}
