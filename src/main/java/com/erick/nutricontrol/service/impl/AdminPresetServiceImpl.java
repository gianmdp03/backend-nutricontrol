package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.dto.adminPreset.AdminPresetDetailDTO;
import com.erick.nutricontrol.dto.adminPreset.AdminPresetRequestDTO;
import com.erick.nutricontrol.exception.NotFoundException;
import com.erick.nutricontrol.mapper.AdminPresetMapper;
import com.erick.nutricontrol.model.AdminPreset;
import com.erick.nutricontrol.repository.AdminPresetRepository;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.service.AdminPresetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminPresetServiceImpl implements AdminPresetService {
  private final AdminPresetRepository repository;
  private final AdminPresetMapper mapper;

  @Override
  @Transactional
  public AdminPresetDetailDTO createOrUpdateAdminPreset(User admin, AdminPresetRequestDTO dto) {
    AdminPreset adminPreset = admin.getAdminPreset();
    if (adminPreset == null) {
      adminPreset = mapper.toEntity(dto);
      adminPreset.setUser(admin);
      admin.setAdminPreset(adminPreset);
    } else {
      adminPreset.setAdminName(dto.adminName());
      adminPreset.setSpecialty(dto.specialty());
      adminPreset.setExequatur(dto.exequatur());
    }
    adminPreset = repository.save(adminPreset);

    return mapper.toDetailDto(adminPreset);
  }

  @Override
  public AdminPresetDetailDTO getAdminPreset(User admin) {
    if (admin.getAdminPreset() == null) {
      throw new NotFoundException("Admin Preset Not Found");
    }
    return mapper.toDetailDto(admin.getAdminPreset());
  }
}
