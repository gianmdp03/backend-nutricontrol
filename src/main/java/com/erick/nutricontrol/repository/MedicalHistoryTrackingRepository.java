package com.erick.nutricontrol.repository;

import com.erick.nutricontrol.model.MedicalHistoryTracking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalHistoryTrackingRepository
    extends JpaRepository<MedicalHistoryTracking, Long> {}
