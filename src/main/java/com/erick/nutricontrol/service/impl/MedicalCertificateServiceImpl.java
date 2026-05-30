package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.dto.medicalCertificate.MedicalCertificateDetailDTO;
import com.erick.nutricontrol.dto.medicalCertificate.MedicalCertificateRequestDTO;
import com.erick.nutricontrol.exception.BadRequestException;
import com.erick.nutricontrol.exception.NotFoundException;
import com.erick.nutricontrol.mapper.MedicalCertificateMapper;
import com.erick.nutricontrol.model.AdminPreset;
import com.erick.nutricontrol.model.MedicalCertificate;
import com.erick.nutricontrol.repository.MedicalCertificateRepository;
import com.erick.nutricontrol.security.user.model.User;
import com.erick.nutricontrol.security.user.repository.UserRepository;
import com.erick.nutricontrol.service.MedicalCertificateService;
import com.erick.nutricontrol.service.PDFGeneratorService;
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
public class MedicalCertificateServiceImpl implements MedicalCertificateService {
  private final MedicalCertificateRepository repository;
  private final MedicalCertificateMapper mapper;
  private final UserRepository userRepository;
  private final PDFGeneratorService pdfGeneratorService;
  private static final DateTimeFormatter FORMATTER =
      DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

  @Override
  @Transactional
  public MedicalCertificateDetailDTO createMedicalCertificate(
      User admin, MedicalCertificateRequestDTO dto) {
    User user =
        userRepository
            .findById(dto.userId())
            .orElseThrow(() -> new NotFoundException("User not found"));
    AdminPreset adminPreset = admin.getAdminPreset();
    MedicalCertificate medicalCertificate = mapper.toEntity(dto);
    medicalCertificate.setAdminName(adminPreset.getAdminName());
    medicalCertificate.setSpecialty(adminPreset.getSpecialty());
    medicalCertificate.setExequatur(adminPreset.getExequatur());
    medicalCertificate.setUser(user);
    medicalCertificate = repository.save(medicalCertificate);
    String date = convertFromUtcToTimezone(medicalCertificate.getDateTime(), user.getTimezone());
    return mapper.toDetailDto(medicalCertificate, date);
  }

  @Override
  public Page<MedicalCertificateDetailDTO> getAllUserMedicalCertificates(
      User user, Pageable pageable) {
    Page<MedicalCertificate> page = repository.findByUser(user, pageable);
    if (page.isEmpty()) {
      return Page.empty();
    }
    String userTimezone = user.getTimezone();
    return page.map(
        certificate -> {
          String formattedDate = convertFromUtcToTimezone(certificate.getDateTime(), userTimezone);
          return mapper.toDetailDto(certificate, formattedDate);
        });
  }

  @Override
  public Page<MedicalCertificateDetailDTO> adminGetUserMedicalCertificates(
      Long userId, Pageable pageable) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    Page<MedicalCertificate> page = repository.findByUser(user, pageable);
    if (page.isEmpty()) {
      return Page.empty();
    }
    String userTimezone = user.getTimezone();
    return page.map(
        medicalCertificate -> {
          String formattedDate =
              convertFromUtcToTimezone(medicalCertificate.getDateTime(), userTimezone);
          return mapper.toDetailDto(medicalCertificate, formattedDate);
        });
  }

  @Override
  @Transactional
  public MedicalCertificateDetailDTO createManualMedicalCertificate(
      User admin, MedicalCertificateRequestDTO dto) {
    AdminPreset adminPreset = admin.getAdminPreset();
    MedicalCertificate medicalCertificate = mapper.toEntity(dto);
    medicalCertificate.setAdminName(adminPreset.getAdminName());
    medicalCertificate.setSpecialty(adminPreset.getSpecialty());
    medicalCertificate.setExequatur(adminPreset.getExequatur());
    medicalCertificate.setUser(null);
    medicalCertificate = repository.save(medicalCertificate);
    String date =
        convertFromUtcToTimezone(medicalCertificate.getDateTime(), "America/Santo_Domingo");
    return mapper.toDetailDto(medicalCertificate, date);
  }

  @Override
  public Page<MedicalCertificateDetailDTO> getManualMedicalCertificates(Pageable pageable) {
    Page<MedicalCertificate> page = repository.findByUserIsNull(pageable);
    if (page.isEmpty()) {
      return Page.empty();
    }
    return page.map(
        certificate -> {
          String formattedDate =
              convertFromUtcToTimezone(certificate.getDateTime(), "America/Santo_Domingo");
          return mapper.toDetailDto(certificate, formattedDate);
        });
  }

  @Override
  public byte[] getPDFMedicalCertificate(User user, Long id) {
    MedicalCertificate medicalCertificate =
        repository
            .findByIdAndUser(id, user)
            .orElseThrow(() -> new NotFoundException("MedicalCertificate not found"));
    String userTimezone = user.getTimezone();
    String date = convertFromUtcToTimezone(medicalCertificate.getDateTime(), userTimezone);
    try {
      return pdfGeneratorService.generateMedicalCertificate(
          medicalCertificate.getPatientName(),
          medicalCertificate.getAge(),
          medicalCertificate.getAdminName(),
          medicalCertificate.getSpecialty(),
          medicalCertificate.getExequatur(),
          medicalCertificate.getTextareaTexto(),
          date);
    } catch (Exception e) {
      throw new BadRequestException("Error al generar el PDF");
    }
  }

  @Override
  public byte[] getManualPDFMedicalCertificate(Long id) {
    MedicalCertificate medicalCertificate =
        repository
            .findByIdAndUserIsNull(id)
            .orElseThrow(() -> new NotFoundException("MedicalCertificate not found"));
    String date =
        convertFromUtcToTimezone(medicalCertificate.getDateTime(), "America/Santo_Domingo");
    try {
      return pdfGeneratorService.generateMedicalCertificate(
          medicalCertificate.getPatientName(),
          medicalCertificate.getAge(),
          medicalCertificate.getAdminName(),
          medicalCertificate.getSpecialty(),
          medicalCertificate.getExequatur(),
          medicalCertificate.getTextareaTexto(),
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
