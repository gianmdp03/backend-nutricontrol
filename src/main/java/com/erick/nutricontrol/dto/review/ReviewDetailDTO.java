package com.erick.nutricontrol.dto.review;


import java.time.OffsetDateTime;

public record ReviewDetailDTO(Long id, int score, String comment, OffsetDateTime date) {}
