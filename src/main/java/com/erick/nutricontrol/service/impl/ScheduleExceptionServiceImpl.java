package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionDetailDTO;
import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionRequestDTO;
import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionUpdateDTO;
import com.erick.nutricontrol.exception.BadRequestException;
import com.erick.nutricontrol.exception.NotFoundException;
import com.erick.nutricontrol.mapper.ScheduleExceptionMapper;
import com.erick.nutricontrol.model.ScheduleException;
import com.erick.nutricontrol.repository.ScheduleExceptionRepository;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.security.user.repository.UserRepository;
import com.erick.nutricontrol.service.ScheduleExceptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleExceptionServiceImpl implements ScheduleExceptionService {
  private final ScheduleExceptionRepository repository;
  private final ScheduleExceptionMapper mapper;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public ScheduleExceptionDetailDTO addScheduleException(User user, ScheduleExceptionRequestDTO dto) {
    if(dto.startTime().isAfter(dto.endTime()) || dto.startTime().equals(dto.endTime())) {
      throw new BadRequestException("Invalid start and end time");
    }
    ScheduleException scheduleException = mapper.toEntity(dto);
    scheduleException.setAdmin(user);
    scheduleException = repository.save(scheduleException);
    return mapper.toDetailDTO(scheduleException);
  }

  @Override
  public Page<ScheduleExceptionDetailDTO> listScheduleExceptionsByAdmin(User user, Pageable pageable) {
    Page<ScheduleException> page = repository.findByAdmin(user, pageable);
    if(page.isEmpty()){
      return Page.empty();
    }
    return page.map(mapper::toDetailDTO);
  }

  @Override
  public ScheduleExceptionDetailDTO getScheduleExceptionById(User user, Long id) {
    ScheduleException scheduleException =
        repository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Schedule Exception Not Found"));
    if(!user.equals(scheduleException.getAdmin())) throw new BadRequestException("Invalid user");
    return mapper.toDetailDTO(scheduleException);
  }

  @Override
  @Transactional
  public ScheduleExceptionDetailDTO updateScheduleException(
          User user, Long id, ScheduleExceptionUpdateDTO dto) {
    if(dto.startTime().isAfter(dto.endTime()) || dto.startTime().equals(dto.endTime())) {
      throw new BadRequestException("Invalid start and end time");
    }
    ScheduleException scheduleException =
        repository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Schedule Exception Not Found"));
    if(!user.equals(scheduleException.getAdmin())) throw new BadRequestException("Invalid user");
    mapper.updateEntityFromDto(dto, scheduleException);
    scheduleException = repository.save(scheduleException);
    return mapper.toDetailDTO(scheduleException);
  }

  @Override
  @Transactional
  public void deleteScheduleException(User user, Long id) {
    ScheduleException scheduleException =
        repository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Schedule Exception Not Found"));
    if(!user.equals(scheduleException.getAdmin())) throw new BadRequestException("Invalid user");
    repository.delete(scheduleException);
  }
}
