package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.extra.DailyMenu;
import com.erick.nutricontrol.service.PDFGeneratorService;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.util.Map;
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

    File tempFontFile = File.createTempFile("Roboto-Regular", ".ttf");
    tempFontFile.deleteOnExit();

    try (InputStream is = fontResource.getInputStream();
        FileOutputStream os = new FileOutputStream(tempFontFile)) {
      is.transferTo(os);
    }

    renderer
        .getFontResolver()
        .addFont(tempFontFile.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

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

  @Override
  public byte[] generateNutritionalPlan(
      String patientName,
      String age,
      String adminName,
      String adminSpecialty,
      Map<DayOfWeek, DailyMenu> weeklyMenu,
      String date,
      String textareaTexto)
      throws Exception {

    Context context = new Context();
    context.setVariable("patientName", patientName);
    context.setVariable("patientAge", age);
    context.setVariable("adminName", adminName);
    context.setVariable("adminSpecialty", adminSpecialty);
    context.setVariable("date", date);
    context.setVariable("textareaTexto", textareaTexto);
    context.setVariable("plan", weeklyMenu);

    String htmlContent = templateEngine.process("nutritionalTemplate", context);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ITextRenderer renderer = new ITextRenderer();

    ClassPathResource fontResource = new ClassPathResource("fonts/Roboto-Regular.ttf");

    File tempFontFile = File.createTempFile("Roboto-Regular", ".ttf");
    tempFontFile.deleteOnExit();

    try (InputStream is = fontResource.getInputStream();
        FileOutputStream os = new FileOutputStream(tempFontFile)) {
      is.transferTo(os);
    }

    renderer
        .getFontResolver()
        .addFont(tempFontFile.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

    renderer.setDocumentFromString(htmlContent);
    renderer.layout();
    renderer.createPDF(outputStream);

    return outputStream.toByteArray();
  }
}
