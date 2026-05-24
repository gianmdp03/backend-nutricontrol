package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionDetailDTO;

import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionRequestDTO;
import com.erick.nutricontrol.dto.scheduleException.ScheduleExceptionUpdateDTO;
import com.erick.nutricontrol.security.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScheduleExceptionService {
    ScheduleExceptionDetailDTO addScheduleException(User user, ScheduleExceptionRequestDTO dto);
    Page<ScheduleExceptionDetailDTO> listScheduleExceptionsByAdmin(User user, Pageable pageable);
    ScheduleExceptionDetailDTO getScheduleExceptionById(User user, Long id);
    ScheduleExceptionDetailDTO updateScheduleException(User user, Long id, ScheduleExceptionUpdateDTO dto);
    void deleteScheduleException(User user, Long id);
}
