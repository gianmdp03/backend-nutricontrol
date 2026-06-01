package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.nutritionalPlan.NutritionalPlanDetailDTO;
import com.erick.nutricontrol.dto.nutritionalPlan.NutritionalPlanRequestDTO;
import com.erick.nutricontrol.security.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NutritionalPlanService {
  NutritionalPlanDetailDTO createNutritionalPlan(User admin, NutritionalPlanRequestDTO dto);

  Page<NutritionalPlanDetailDTO> getAllUserNutritionalPlans(User user, Pageable pageable);

  Page<NutritionalPlanDetailDTO> adminGetUserNutritionalPlans(Long userId, Pageable pageable);

  NutritionalPlanDetailDTO createManualNutritionalPlan(User admin, NutritionalPlanRequestDTO dto);

  Page<NutritionalPlanDetailDTO> getManualNutritionalPlans(Pageable pageable);

  byte[] getPDFNutritionalPlan(User user, Long id);

  byte[] getManualPDFNutritionalPlan(Long id);
}
