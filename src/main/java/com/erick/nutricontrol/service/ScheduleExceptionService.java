package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionDetailDTO;

import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionRequestDTO;
import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScheduleExceptionService {
    ScheduleExceptionDetailDTO addScheduleException(ScheduleExceptionRequestDTO dto);
    Page<ScheduleExceptionDetailDTO> listScheduleExceptions(Pageable pageable);
    ScheduleExceptionDetailDTO getScheduleExceptionById(Long id);
    ScheduleExceptionDetailDTO updateScheduleException(Long id, ScheduleExceptionUpdateDTO dto);
    void deleteScheduleException(Long id);
}
