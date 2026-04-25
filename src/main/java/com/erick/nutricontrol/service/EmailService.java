package com.erick.nutricontrol.service;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
