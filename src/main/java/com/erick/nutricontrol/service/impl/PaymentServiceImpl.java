package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.repository.PaymentRepository;
import com.erick.nutricontrol.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository repository;
}
