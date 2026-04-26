package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionDetailDTO;
import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionRequestDTO;
import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionUpdateDTO;
import com.erick.nutricontrol.exception.NotFoundException;
import com.erick.nutricontrol.mapper.ScheduleExceptionMapper;
import com.erick.nutricontrol.model.ScheduleException;
import com.erick.nutricontrol.repository.ScheduleExceptionRepository;
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

  @Override
  @Transactional
  public ScheduleExceptionDetailDTO addScheduleException(ScheduleExceptionRequestDTO dto) {
    ScheduleException scheduleException = mapper.toEntity(dto);
    scheduleException = repository.save(scheduleException);
    return mapper.toDetailDTO(scheduleException);
  }

  @Override
  public Page<ScheduleExceptionDetailDTO> listScheduleExceptions(Pageable pageable) {
    Page<ScheduleException> scheduleExceptions = repository.findAll(pageable);
    if (scheduleExceptions.isEmpty()) {
      return Page.empty();
    }
    return scheduleExceptions.map(mapper::toDetailDTO);
  }

  @Override
  public ScheduleExceptionDetailDTO getScheduleExceptionById(Long id) {
    ScheduleException scheduleException =
        repository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Schedule Exception Not Found"));
    return mapper.toDetailDTO(scheduleException);
  }

  @Override
  @Transactional
  public ScheduleExceptionDetailDTO updateScheduleException(
      Long id, ScheduleExceptionUpdateDTO dto) {
    ScheduleException scheduleException =
        repository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Schedule Exception Not Found"));
    mapper.updateEntityFromDto(dto, scheduleException);
    scheduleException = repository.save(scheduleException);
    return mapper.toDetailDTO(scheduleException);
  }

  @Override
  @Transactional
  public void deleteScheduleException(Long id) {
    ScheduleException scheduleException =
        repository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Schedule Exception Not Found"));
    repository.delete(scheduleException);
  }
}
