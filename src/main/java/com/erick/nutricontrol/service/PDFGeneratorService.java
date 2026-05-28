package com.erick.nutricontrol.service;

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
}
