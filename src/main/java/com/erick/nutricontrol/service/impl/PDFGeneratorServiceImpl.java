package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.service.PDFGeneratorService;
import lombok.RequiredArgsConstructor;
import org.openpdf.text.pdf.BaseFont;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PDFGeneratorServiceImpl implements PDFGeneratorService {
    private final TemplateEngine templateEngine;

    @Override
    public byte[] generateAppointmentReceipt(String patientName, String date, String time, String doctorName) throws Exception {
        Context context = new Context();
        context.setVariable("patientName", patientName);
        context.setVariable("appointmentDate", date);
        context.setVariable("appointmentTime", time);
        context.setVariable("doctorName", doctorName);

        String htmlContent = templateEngine.process("voucher", context);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();

        ClassPathResource fontResource = new ClassPathResource("fonts/Roboto-Regular.ttf");
        renderer.getFontResolver().addFont(
                fontResource.getURL().toString(),
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED
        );

        renderer.setDocumentFromString(htmlContent);
        renderer.layout();
        renderer.createPDF(outputStream);

        return outputStream.toByteArray();
    }
}