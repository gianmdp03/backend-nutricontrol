package com.erick.nutricontrol.service.impl;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PDFGeneratorServiceImpl {

    private final TemplateEngine templateEngine;

    public PDFGeneratorServiceImpl(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public byte[] generateAppointmentReceipt(String patientName, String date, String serviceName) throws Exception {
        Context context = new Context();
        context.setVariable("patientName", patientName);
        context.setVariable("appointmentDate", date);
        context.setVariable("serviceName", serviceName);

        String htmlContent = templateEngine.process("comprobante", context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream.toByteArray();
    }
}