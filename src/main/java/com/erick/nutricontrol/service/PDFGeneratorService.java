package com.erick.nutricontrol.service;

public interface PDFGeneratorService {
    byte[] generateAppointmentReceipt(String patientName, String date, String doctorName) throws Exception;
}
