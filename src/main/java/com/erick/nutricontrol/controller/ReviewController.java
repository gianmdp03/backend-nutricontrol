package com.erick.nutricontrol.controller;

import com.erick.nutricontrol.dto.review.ReviewDetailDTO;
import com.erick.nutricontrol.dto.review.ReviewRequestDTO;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
  private final ReviewService reviewService;

  @PreAuthorize("hasAuthority('ROLE_PATIENT')")
  @PostMapping
  public ResponseEntity<ReviewDetailDTO> addReview(
      @AuthenticationPrincipal User user, @Valid @RequestBody ReviewRequestDTO dto) {
    return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.addReview(user, dto));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @GetMapping("/admin")
  public ResponseEntity<Page<ReviewDetailDTO>> listAdminReviews(
      @AuthenticationPrincipal User admin,
      @PageableDefault(page = 0, size = 24, sort = "date", direction = Sort.Direction.DESC)
          Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(reviewService.listAdminReviews(admin, pageable));
  }
}
