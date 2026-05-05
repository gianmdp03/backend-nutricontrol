package com.erick.nutricontrol.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendEmailWithReceipt(String to, String subject, String body, byte[] pdfBytes) throws Exception;
}
