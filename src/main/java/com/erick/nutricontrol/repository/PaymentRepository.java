package com.erick.nutricontrol.repository;

import com.erick.nutricontrol.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaypalOrderId(String paypalOrderId);
    Optional<Payment> findByPaypalAuthorizationId(String paypalAuthorizationId);
    Optional<Payment> findByPaypalCaptureId(String paypalCaptureId);
}
