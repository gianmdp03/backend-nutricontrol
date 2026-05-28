package com.erick.nutricontrol.task;

import com.erick.nutricontrol.repository.MedicalCertificateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MedicalCertificateTasks {
  private final MedicalCertificateRepository repository;
}
