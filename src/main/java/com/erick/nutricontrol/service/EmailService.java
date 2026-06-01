package com.erick.nutricontrol.service;

import java.util.Map;

public interface EmailService {
  void sendEmail(String to, String subject, String body);

  void sendEmailWithReceipt(String to, String subject, String body, byte[] pdfBytes)
      throws Exception;

  void sendHtmlTemplateEmail(
      String to, String subject, String templateName, Map<String, Object> variables);

  void sendAppointmentReceiptAsync(
      String to, String patientName, String date, String time, String doctorName, String meetLink);
}
