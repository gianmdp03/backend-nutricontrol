package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleDetailDTO;
import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleRequestDTO;
import com.erick.nutricontrol.dto.scheduleRule.ScheduleRuleUpdateDTO;
import com.erick.nutricontrol.exception.BadRequestException;
import com.erick.nutricontrol.exception.ConflictException;
import com.erick.nutricontrol.exception.NotFoundException;
import com.erick.nutricontrol.mapper.ScheduleRuleMapper;
import com.erick.nutricontrol.model.ScheduleRule;
import com.erick.nutricontrol.repository.ScheduleRuleRepository;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.security.user.repository.UserRepository;
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
  private final UserRepository userRepository;

  @Override
  @Transactional
  public ScheduleRuleDetailDTO addScheduleRule(String username, ScheduleRuleRequestDTO dto) {

    if (!dto.endTime().isAfter(dto.startTime())) {
      throw new BadRequestException("El horario de fin debe ser posterior al horario de inicio. Si trabaja cruzando la medianoche, cree dos reglas (Ej: 22:00 a 23:59 y 00:00 a 02:00).");
    }

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("User not found"));

    List<ScheduleRule> list = repository.findByAdminAndDayOfWeek(user, dto.dayOfWeek());

    LocalTime newStart = dto.startTime();
    LocalTime newEnd = dto.endTime();

    for (ScheduleRule existing : list) {
      boolean overlaps = newStart.isBefore(existing.getEndTime()) && existing.getStartTime().isBefore(newEnd);

      if (overlaps) {
        throw new ConflictException("Ya existe una regla de horario que se superpone con este rango ("
                + existing.getStartTime() + " a " + existing.getEndTime() + ").");
      }
    }

    ScheduleRule scheduleRule = mapper.toEntity(dto);
    scheduleRule.setAdmin(user);
    scheduleRule = repository.save(scheduleRule);

    return mapper.toDetailDTO(scheduleRule);
  }

  @Override
  public Page<ScheduleRuleDetailDTO> listScheduleRulesByAdmin(String username, Pageable pageable) {
    User user = userRepository.findByUsername(username).orElseThrow(()-> new NotFoundException("User not found"));
    Page<ScheduleRule> page = repository.findByAdmin(user, pageable);
    if(page.isEmpty()){
      return Page.empty();
    }
    return page.map(mapper::toDetailDTO);
  }

  @Override
  public ScheduleRuleDetailDTO getScheduleRuleById(String username, Long id) {
    User user = userRepository.findByUsername(username).orElseThrow(()-> new NotFoundException("User not found"));
    ScheduleRule scheduleRule = repository.findById(id).orElseThrow(() -> new NotFoundException("Schedule Rule Not Found"));
    if(!user.equals(scheduleRule.getAdmin())) throw new BadRequestException("Invalid user");
    return mapper.toDetailDTO(scheduleRule);
  }

  @Override
  @Transactional
  public ScheduleRuleDetailDTO updateScheduleRule(String username, Long id, ScheduleRuleUpdateDTO dto){
    if(dto.startTime().isAfter(dto.endTime()) || dto.startTime().equals(dto.endTime())) {
      throw new BadRequestException("Invalid start and end time");
    }
    User user = userRepository.findByUsername(username).orElseThrow(()-> new NotFoundException("User not found"));
    ScheduleRule scheduleRule = repository.findById(id).orElseThrow(() -> new NotFoundException("Schedule Rule Not Found"));
    if(!user.equals(scheduleRule.getAdmin())) {
      throw new BadRequestException("Invalid user");
    }
    mapper.updateEntityFromDto(dto, scheduleRule);
    scheduleRule = repository.save(scheduleRule);
    return mapper.toDetailDTO(scheduleRule);
  }

  @Override
  @Transactional
  public void deleteScheduleRuleById(String username, Long id) {
    User user = userRepository.findByUsername(username).orElseThrow(()-> new NotFoundException("User not found"));
    ScheduleRule scheduleRule = repository.findById(id).orElseThrow(() -> new NotFoundException("Schedule Rule Not Found"));
    if(!user.equals(scheduleRule.getAdmin())) {
      throw new BadRequestException("Invalid user");
    }
    repository.delete(scheduleRule);
  }
}
