package com.erick.nutricontrol.mapper;


import com.erick.nutricontrol.dto.payment.PaymentDetailDTO;
import com.erick.nutricontrol.dto.payment.PaymentRequestDTO;
import com.erick.nutricontrol.dto.payment.PaymentUpdateDTO;
import com.erick.nutricontrol.model.Payment;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public abstract class PaymentMapper {
    @Mapping(target = "id", ignore = true)
    public abstract Payment toEntity(PaymentRequestDTO dto);
    public abstract PaymentDetailDTO toDetailDTO(Payment entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    public abstract void updateEntityFromDto(PaymentUpdateDTO dto, @MappingTarget Payment entity);
}
