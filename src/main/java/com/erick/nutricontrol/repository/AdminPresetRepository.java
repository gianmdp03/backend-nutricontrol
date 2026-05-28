package com.erick.nutricontrol.repository;

import com.erick.nutricontrol.model.AdminPreset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminPresetRepository extends JpaRepository<AdminPreset, Long> {
    
}
