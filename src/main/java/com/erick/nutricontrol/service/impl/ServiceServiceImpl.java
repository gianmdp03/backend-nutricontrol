package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.repository.ServiceRepository;
import com.erick.nutricontrol.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServiceServiceImpl implements ServiceService {
    private final ServiceRepository repository;
}
