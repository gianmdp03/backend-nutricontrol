package com.erick.nutricontrol.mapper;

import com.erick.nutricontrol.dto.notification.NotificationDetailDTO;
import com.erick.nutricontrol.model.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class NotificationMapper {
    public abstract NotificationDetailDTO toDetailDTO(Notification entity);
}
