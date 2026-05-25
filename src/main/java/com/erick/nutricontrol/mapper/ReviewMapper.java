package com.erick.nutricontrol.mapper;

import com.erick.nutricontrol.dto.review.ReviewDetailDTO;
import com.erick.nutricontrol.dto.review.ReviewRequestDTO;
import com.erick.nutricontrol.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class ReviewMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "date", ignore = true)
    public abstract Review toEntity(ReviewRequestDTO dto);
    public abstract ReviewDetailDTO toDetailDto(Review entity);
}
