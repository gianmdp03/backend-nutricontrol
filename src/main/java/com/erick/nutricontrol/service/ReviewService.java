package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.review.ReviewDetailDTO;
import com.erick.nutricontrol.dto.review.ReviewRequestDTO;
import com.erick.nutricontrol.security.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    ReviewDetailDTO addReview(User user, ReviewRequestDTO dto);
    Page<ReviewDetailDTO> listAdminReviews(User admin, Pageable pageable);
}
