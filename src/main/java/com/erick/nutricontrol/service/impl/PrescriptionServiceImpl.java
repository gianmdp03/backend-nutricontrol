package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.dto.prescription.PrescriptionDetailDTO;
import com.erick.nutricontrol.dto.prescription.PrescriptionRequestDTO;
import com.erick.nutricontrol.exception.BadRequestException;
import com.erick.nutricontrol.exception.NotFoundException;
import com.erick.nutricontrol.mapper.PrescriptionMapper;
import com.erick.nutricontrol.model.AdminPreset;
import com.erick.nutricontrol.model.Prescription;
import com.erick.nutricontrol.repository.PrescriptionRepository;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.security.user.repository.UserRepository;
import com.erick.nutricontrol.service.PDFGeneratorService;
import com.erick.nutricontrol.service.PrescriptionService;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {
  private final PrescriptionRepository repository;
  private final PrescriptionMapper mapper;
  private final UserRepository userRepository;
  private final PDFGeneratorService pdfGeneratorService;
  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

  @Override
  @Transactional
  public PrescriptionDetailDTO createPrescription(User admin, PrescriptionRequestDTO dto) {
    User user =
        userRepository
            .findById(dto.userId())
            .orElseThrow(() -> new NotFoundException("User not found"));
    AdminPreset adminPreset = admin.getAdminPreset();
    Prescription prescription = mapper.toEntity(dto);
    prescription.setAdminName(adminPreset.getAdminName());
    prescription.setSpecialty(adminPreset.getSpecialty());
    prescription.setExequatur(adminPreset.getExequatur());
    prescription.setUser(user);
    prescription = repository.save(prescription);
    String date = convertFromUtcToTimezone(prescription.getDateTime(), user.getTimezone());
    return mapper.toDetailDto(prescription, date);
  }

  @Override
  public Page<PrescriptionDetailDTO> getAllUserPrescriptions(User user, Pageable pageable) {
    Page<Prescription> page = repository.findByUser(user, pageable);
    if (page.isEmpty()) {
      return Page.empty();
    }
    String userTimezone = user.getTimezone();
    return page.map(
        prescription -> {
          String formattedDate = convertFromUtcToTimezone(prescription.getDateTime(), userTimezone);
          return mapper.toDetailDto(prescription, formattedDate);
        });
  }

  @Override
  public Page<PrescriptionDetailDTO> adminGetUserPrescriptions(Long userId, Pageable pageable) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    Page<Prescription> page = repository.findByUser(user, pageable);
    if (page.isEmpty()) {
      return Page.empty();
    }
    String userTimezone = user.getTimezone();
    return page.map(
        prescription -> {
          String formattedDate = convertFromUtcToTimezone(prescription.getDateTime(), userTimezone);
          return mapper.toDetailDto(prescription, formattedDate);
        });
  }

  @Override
  public byte[] getPDFPrescription(User user, Long id) {
    Prescription prescription =
        repository
            .findByIdAndUser(id, user)
            .orElseThrow(() -> new NotFoundException("MedicalCertificate not found"));
    String userTimezone = user.getTimezone();
    String date = convertFromUtcToTimezone(prescription.getDateTime(), userTimezone);
    try {
      return pdfGeneratorService.generatePrescription(
          prescription.getPatientName(),
          prescription.getAge(),
          prescription.getAdminName(),
          prescription.getSpecialty(),
          prescription.getExequatur(),
          prescription.getTextareaTexto(),
          date);
    } catch (Exception e) {
      throw new BadRequestException("Error al generar el PDF");
    }
  }

  private String convertFromUtcToTimezone(OffsetDateTime dateTime, String timezone) {
    ZonedDateTime zonedDateTime = dateTime.atZoneSameInstant(ZoneId.of(timezone));
    return zonedDateTime.format(FORMATTER);
  }
}
