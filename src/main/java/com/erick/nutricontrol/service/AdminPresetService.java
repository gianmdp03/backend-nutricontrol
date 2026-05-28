package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.adminPreset.AdminPresetDetailDTO;
import com.erick.nutricontrol.dto.adminPreset.AdminPresetRequestDTO;
import com.erick.nutricontrol.security.user.model.User;

public interface AdminPresetService {
  AdminPresetDetailDTO createOrUpdateAdminPreset(User admin, AdminPresetRequestDTO dto);

  AdminPresetDetailDTO getAdminPreset(User admin);
}
