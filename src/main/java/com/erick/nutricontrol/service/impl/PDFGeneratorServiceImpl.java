package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.service.PDFGeneratorService;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.openpdf.text.pdf.BaseFont;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Service
@RequiredArgsConstructor
public class PDFGeneratorServiceImpl implements PDFGeneratorService {
  private final TemplateEngine templateEngine;
  private static final String RECETA_TEMPLATE = "templates/receta_medica.pdf";

  @Override
  public byte[] generateAppointmentReceipt(
      String patientName, String date, String time, String doctorName) throws Exception {
    Context context = new Context();
    context.setVariable("patientName", patientName);
    context.setVariable("appointmentDate", date);
    context.setVariable("appointmentTime", time);
    context.setVariable("doctorName", doctorName);

    String htmlContent = templateEngine.process("voucher", context);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ITextRenderer renderer = new ITextRenderer();

    ClassPathResource fontResource = new ClassPathResource("fonts/Roboto-Regular.ttf");
    renderer
        .getFontResolver()
        .addFont(fontResource.getURL().toString(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

    renderer.setDocumentFromString(htmlContent);
    renderer.layout();
    renderer.createPDF(outputStream);

    return outputStream.toByteArray();
  }

  @Override
  public byte[] generateMedicalCertificate(
      String patientName,
      String age,
      String adminName,
      String specialty,
      String exequatur,
      String textareaTexto,
      String date)
      throws Exception {
    ClassPathResource resource = new ClassPathResource(RECETA_TEMPLATE);

    try (InputStream is = resource.getInputStream();
        PDDocument document = Loader.loadPDF(is.readAllBytes())) {

      PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
      if (acroForm != null) {
        acroForm.getField("patientName").setValue(patientName);
        acroForm.getField("patientAge").setValue(age);
        acroForm.getField("adminName").setValue(adminName);
        acroForm.getField("adminEspecialidad").setValue(specialty);
        acroForm.getField("adminExequatur").setValue(exequatur);
        acroForm.getField("fechaReceta").setValue(date);
        acroForm.getField("textareaTexto").setValue(textareaTexto);

        acroForm.flatten();
      }

      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        document.save(baos);
        return baos.toByteArray();
      }
    }
  }

  @Override
  public byte[] generatePrescription(
      String patientName,
      String age,
      String adminName,
      String specialty,
      String exequatur,
      String textareaTexto,
      String date)
      throws Exception {
    ClassPathResource resource = new ClassPathResource(RECETA_TEMPLATE);

    try (InputStream is = resource.getInputStream();
        PDDocument document = Loader.loadPDF(is.readAllBytes())) {

      PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
      if (acroForm != null) {
        acroForm.getField("patientName").setValue(patientName);
        acroForm.getField("patientAge").setValue(age);
        acroForm.getField("adminName").setValue(adminName);
        acroForm.getField("adminEspecialidad").setValue(specialty);
        acroForm.getField("adminExequatur").setValue(exequatur);
        acroForm.getField("fechaReceta").setValue(date);
        acroForm.getField("textareaTexto").setValue(textareaTexto);

        acroForm.flatten();
      }

      try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        document.save(baos);
        return baos.toByteArray();
      }
    }
  }
}
