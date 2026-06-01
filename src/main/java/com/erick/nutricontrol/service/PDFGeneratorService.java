package com.erick.nutricontrol.service;

import com.erick.nutricontrol.extra.DailyMenu;
import java.time.DayOfWeek;
import java.util.Map;

public interface PDFGeneratorService {
  byte[] generateAppointmentReceipt(String patientName, String date, String time, String doctorName)
      throws Exception;

  byte[] generateMedicalCertificate(
      String patientName,
      String age,
      String adminName,
      String specialty,
      String exequatur,
      String textareaTexto,
      String date)
      throws Exception;

  byte[] generatePrescription(
      String patientName,
      String age,
      String adminName,
      String specialty,
      String exequatur,
      String textareaTexto,
      String date)
      throws Exception;

  byte[] generateNutritionalPlan(
      String patientName,
      String age,
      String adminName,
      String adminSpecialty,
      Map<DayOfWeek, DailyMenu> weeklyMenu,
      String date,
      String textareaTexto)
      throws Exception;
}
