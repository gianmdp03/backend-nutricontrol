package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.dto.nutritionalPlan.NutritionalPlanDetailDTO;
import com.erick.nutricontrol.dto.nutritionalPlan.NutritionalPlanRequestDTO;
import com.erick.nutricontrol.exception.BadRequestException;
import com.erick.nutricontrol.exception.NotFoundException;
import com.erick.nutricontrol.extra.DatetimeConverter;
import com.erick.nutricontrol.mapper.NutritionalPlanMapper;
import com.erick.nutricontrol.model.AdminPreset;
import com.erick.nutricontrol.model.NutritionalPlan;
import com.erick.nutricontrol.repository.NutritionalPlanRepository;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.security.user.repository.UserRepository;
import com.erick.nutricontrol.service.NutritionalPlanService;
import com.erick.nutricontrol.service.PDFGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NutritionalPlanServiceImpl implements NutritionalPlanService {
  private final NutritionalPlanRepository repository;
  private final NutritionalPlanMapper mapper;
  private final UserRepository userRepository;
  private final PDFGeneratorService pdfGeneratorService;

  @Override
  @Transactional
  public NutritionalPlanDetailDTO createNutritionalPlan(User admin, NutritionalPlanRequestDTO dto) {
    User user =
        userRepository
            .findById(dto.userId())
            .orElseThrow(() -> new NotFoundException("User not found"));
    AdminPreset adminPreset = admin.getAdminPreset();
    NutritionalPlan nutritionalPlan = mapper.toEntity(dto);
    nutritionalPlan.setUser(user);
    nutritionalPlan.setAdminName(adminPreset.getAdminName());
    nutritionalPlan.setAdminSpecialty(adminPreset.getSpecialty());
    nutritionalPlan = repository.save(nutritionalPlan);
    String date =
        DatetimeConverter.convertFromUtcToTimezone(
            nutritionalPlan.getDateTime(), user.getTimezone());
    return mapper.toDetailDto(nutritionalPlan, date);
  }

  @Override
  public Page<NutritionalPlanDetailDTO> getAllUserNutritionalPlans(User user, Pageable pageable) {
    Page<NutritionalPlan> page = repository.findByUser(user, pageable);
    if (page.isEmpty()) {
      return Page.empty();
    }
    String userTimezone = user.getTimezone();
    return page.map(
        nutritionalPlan -> {
          String formattedDate =
              DatetimeConverter.convertFromUtcToTimezone(
                  nutritionalPlan.getDateTime(), userTimezone);
          return mapper.toDetailDto(nutritionalPlan, formattedDate);
        });
  }

  @Override
  public Page<NutritionalPlanDetailDTO> adminGetUserNutritionalPlans(
      Long userId, Pageable pageable) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    Page<NutritionalPlan> page = repository.findByUser(user, pageable);
    if (page.isEmpty()) {
      return Page.empty();
    }
    String userTimezone = user.getTimezone();
    return page.map(
        nutritionalPlan -> {
          String formattedDate =
              DatetimeConverter.convertFromUtcToTimezone(
                  nutritionalPlan.getDateTime(), userTimezone);
          return mapper.toDetailDto(nutritionalPlan, formattedDate);
        });
  }

  @Override
  @Transactional
  public NutritionalPlanDetailDTO createManualNutritionalPlan(
      User admin, NutritionalPlanRequestDTO dto) {
    AdminPreset adminPreset = admin.getAdminPreset();
    NutritionalPlan nutritionalPlan = mapper.toEntity(dto);
    nutritionalPlan.setUser(null);
    nutritionalPlan.setAdminName(adminPreset.getAdminName());
    nutritionalPlan.setAdminSpecialty(adminPreset.getSpecialty());
    nutritionalPlan = repository.save(nutritionalPlan);
    String date =
        DatetimeConverter.convertFromUtcToTimezone(
            nutritionalPlan.getDateTime(), "America/Santo_Domingo");
    return mapper.toDetailDto(nutritionalPlan, date);
  }

  @Override
  public Page<NutritionalPlanDetailDTO> getManualNutritionalPlans(Pageable pageable) {
    Page<NutritionalPlan> page = repository.findByUserIsNull(pageable);
    if (page.isEmpty()) {
      return Page.empty();
    }
    return page.map(
        nutritionalPlan -> {
          String formattedDate =
              DatetimeConverter.convertFromUtcToTimezone(
                  nutritionalPlan.getDateTime(), "America/Santo_Domingo");
          return mapper.toDetailDto(nutritionalPlan, formattedDate);
        });
  }

  @Override
  public byte[] getPDFNutritionalPlan(User user, Long id) {
    NutritionalPlan nutritionalPlan =
        repository
            .findByIdAndUser(id, user)
            .orElseThrow(() -> new NotFoundException("NutritionalPlan not found"));
    String userTimezone = user.getTimezone();
    String date =
        DatetimeConverter.convertFromUtcToTimezone(nutritionalPlan.getDateTime(), userTimezone);
    try {
      return pdfGeneratorService.generateNutritionalPlan(
          nutritionalPlan.getPatientName(),
          nutritionalPlan.getAge(),
          nutritionalPlan.getAdminName(),
          nutritionalPlan.getAdminSpecialty(),
          nutritionalPlan.getWeeklyMenu(),
          date,
          nutritionalPlan.getTextareaTexto());
    } catch (Exception e) {
      throw new BadRequestException("Error al generar el PDF");
    }
  }

  @Override
  public byte[] getManualPDFNutritionalPlan(Long id) {
    NutritionalPlan nutritionalPlan =
        repository
            .findByIdAndUserIsNull(id)
            .orElseThrow(() -> new NotFoundException("NutritionalPlan not found"));
    String date =
        DatetimeConverter.convertFromUtcToTimezone(
            nutritionalPlan.getDateTime(), "America/Santo_Domingo");
    try {
      return pdfGeneratorService.generateNutritionalPlan(
          nutritionalPlan.getPatientName(),
          nutritionalPlan.getAge(),
          nutritionalPlan.getAdminName(),
          nutritionalPlan.getAdminSpecialty(),
          nutritionalPlan.getWeeklyMenu(),
          date,
          nutritionalPlan.getTextareaTexto());
    } catch (Exception e) {
      throw new BadRequestException("Error al generar el PDF");
    }
  }
}
