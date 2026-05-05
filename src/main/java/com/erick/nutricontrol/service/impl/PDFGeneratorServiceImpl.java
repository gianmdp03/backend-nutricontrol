package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.service.PDFGeneratorService;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.ByteArrayOutputStream;

@Service
public class PDFGeneratorServiceImpl implements PDFGeneratorService {

    private final TemplateEngine templateEngine;

    public PDFGeneratorServiceImpl(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    @Override
    public byte[] generateAppointmentReceipt(String patientName, String date, String doctorName) throws Exception {
        Context context = new Context();
        context.setVariable("patientName", patientName);
        context.setVariable("appointmentDate", date);
        context.setVariable("appointmentTime", date);
        context.setVariable("doctorName", doctorName);

        String htmlContent = templateEngine.process("voucher", context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream.toByteArray();
    }
}