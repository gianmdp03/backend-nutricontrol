package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.dto.review.ReviewDetailDTO;
import com.erick.nutricontrol.dto.review.ReviewRequestDTO;
import com.erick.nutricontrol.exception.NotFoundException;
import com.erick.nutricontrol.mapper.ReviewMapper;
import com.erick.nutricontrol.model.Appointment;
import com.erick.nutricontrol.model.Review;
import com.erick.nutricontrol.repository.AppointmentRepository;
import com.erick.nutricontrol.repository.ReviewRepository;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository repository;
    private final ReviewMapper mapper;
    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional
    public ReviewDetailDTO addReview(User user, ReviewRequestDTO dto) {
        Review review = mapper.toEntity(dto);
        Appointment appointment = appointmentRepository.findById(dto.appointmentId()).orElseThrow(()-> new NotFoundException("Appointment not found"));
        review.setAppointment(appointment);
        review.setUser(user);
        review.setAdmin(appointment.getAdmin());
        review = repository.save(review);
        return mapper.toDetailDto(review);
    }

    @Override
    public Page<ReviewDetailDTO> listAdminReviews(User admin, Pageable pageable) {
        Page<Review> page = repository.findByAdmin(admin, pageable);
        if(page.isEmpty()){
            return Page.empty();
        }
        return page.map(mapper::toDetailDto);
    }
}
