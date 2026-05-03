package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionDetailDTO;

import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionRequestDTO;
import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScheduleExceptionService {
    ScheduleExceptionDetailDTO addScheduleException(String username, ScheduleExceptionRequestDTO dto);
    Page<ScheduleExceptionDetailDTO> listScheduleExceptionsByAdmin(String username, Pageable pageable);
    ScheduleExceptionDetailDTO getScheduleExceptionById(String username, Long id);
    ScheduleExceptionDetailDTO updateScheduleException(String username, Long id, ScheduleExceptionUpdateDTO dto);
    void deleteScheduleException(String username, Long id);
}
