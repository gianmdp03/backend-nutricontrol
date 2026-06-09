package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.extra.*;
import com.erick.nutricontrol.model.MedicalHistory;
import com.erick.nutricontrol.model.MedicalHistoryTracking;
import com.erick.nutricontrol.service.PDFGeneratorService;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
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

  @Override
  public byte[] generateMedicalHistory(MedicalHistory medicalHistory, Long trackingId) throws Exception {
    Context context = new Context();
    Map<String, Object> historia = new HashMap<>();

    Function<Object, String> safeStr = val ->
            (val != null && !val.toString().trim().isEmpty()) ? val.toString() : "---";

    MedicalHistoryTracking selectedTracking = null;
    if (medicalHistory.getTrackings() != null && !medicalHistory.getTrackings().isEmpty()) {
      if (trackingId != null) {
        selectedTracking = medicalHistory.getTrackings().stream()
                .filter(t -> t.getId().equals(trackingId))
                .findFirst()
                .orElse(medicalHistory.getTrackings().iterator().next());
      } else {
        selectedTracking = medicalHistory.getTrackings().iterator().next();
      }
    }

    if (medicalHistory.getPatientData() != null) {
      PatientData pd = medicalHistory.getPatientData();
      String fullName = safeStr.apply(pd.fullName());
      historia.put("nombre", fullName.trim().equals("--- ---") ? "---" : fullName);
      historia.put("cedula", safeStr.apply(pd.nationalId()));
      historia.put("edad", safeStr.apply(pd.age()));
      historia.put("sexo", safeStr.apply(pd.gender()));
      historia.put("estadoCivil", safeStr.apply(pd.maritalStatus()));
      historia.put("direccion", safeStr.apply(pd.address()));
      historia.put("telefono", safeStr.apply(pd.phoneNumber()));
      historia.put("seguroMedico", safeStr.apply(pd.healthInsurance()));
      historia.put("ocupacion", safeStr.apply(pd.occupation()));
      historia.put("contactoEmergencia", safeStr.apply(pd.emergencyContact()));
    } else {
      String[] pdKeys = {"nombre", "cedula", "edad", "sexo", "estadoCivil", "direccion", "telefono", "seguroMedico", "ocupacion", "contactoEmergencia"};
      for(String k : pdKeys) historia.put(k, "---");
    }

    historia.put("alergias", safeStr.apply(medicalHistory.getAllergies()));

    if (selectedTracking != null) {
      historia.put("motivoConsulta", safeStr.apply(selectedTracking.getConsultationReason()));

      historia.put("enfermedadActual", safeStr.apply(medicalHistory.getCurrentIllnessHistory()));
      historia.put("resultadosAnaliticas", safeStr.apply(selectedTracking.getLabResultsAndImages()));
      historia.put("impresionDiagnostica", safeStr.apply(selectedTracking.getDiagnosticImpression()));
      historia.put("planMedico", safeStr.apply(selectedTracking.getMedicalPlan()));
    } else {
      historia.put("motivoConsulta", "---");
      historia.put("enfermedadActual", safeStr.apply(medicalHistory.getCurrentIllnessHistory()));
      historia.put("resultadosAnaliticas", "---");
      historia.put("impresionDiagnostica", "---");
      historia.put("planMedico", "---");
    }

    if (medicalHistory.getToxicHabits() != null) {
      ToxicHabits th = medicalHistory.getToxicHabits();
      historia.put("habitoCafe", safeStr.apply(th.coffee()));
      historia.put("habitoTe", safeStr.apply(th.tea()));
      historia.put("habitoAlcohol", safeStr.apply(th.alcohol()));
      historia.put("habitoCigarrillos", safeStr.apply(th.cigarettes()));
      historia.put("habitoDrogas", safeStr.apply(th.drugs()));
      historia.put("indiceTabaquico", safeStr.apply(th.smokingIndex()));
    } else {
      String[] thKeys = {"habitoCafe", "habitoTe", "habitoAlcohol", "habitoCigarrillos", "habitoDrogas", "indiceTabaquico"};
      for(String k : thKeys) historia.put(k, "---");
    }

    if (medicalHistory.getFamilyHistory() != null) {
      FamilyHistory fh = medicalHistory.getFamilyHistory();
      historia.put("antFamPadre", safeStr.apply(fh.father()));
      historia.put("antFamMadre", safeStr.apply(fh.mother()));
      historia.put("antFamAbuelos", safeStr.apply(fh.grandparents()));
      historia.put("antFamOtros", safeStr.apply(fh.others()));
    } else {
      String[] fhKeys = {"antFamPadre", "antFamMadre", "antFamAbuelos", "antFamOtros"};
      for(String k : fhKeys) historia.put(k, "---");
    }

    if (medicalHistory.getSystemReview() != null) {
      SystemReview sr = medicalHistory.getSystemReview();
      historia.put("revCabeza", safeStr.apply(sr.head()));
      historia.put("revOjos", safeStr.apply(sr.eyes()));
      historia.put("revOidos", safeStr.apply(sr.ears()));
      historia.put("revNariz", safeStr.apply(sr.nose()));
      historia.put("revBoca", safeStr.apply(sr.mouthAndThroat()));
      historia.put("revCuello", safeStr.apply(sr.neck()));
      historia.put("revTorax", safeStr.apply(sr.thorax()));
      historia.put("revPulmones", safeStr.apply(sr.lungs()));
      historia.put("revCorazon", safeStr.apply(sr.heart()));
      historia.put("revAbdomen", safeStr.apply(sr.abdomen()));
      historia.put("revGenitourinario", safeStr.apply(sr.genitourinary()));
      historia.put("revExtremidades", safeStr.apply(sr.extremities()));
      historia.put("revMusculo", safeStr.apply(sr.musculoskeletal()));
      historia.put("revNeurologico", safeStr.apply(sr.neurological()));
      historia.put("revPiel", safeStr.apply(sr.skin()));
      historia.put("revEstadoGen", safeStr.apply(sr.generalStatus()));
    } else {
      String[] srKeys = {"revCabeza", "revOjos", "revOidos", "revNariz", "revBoca", "revCuello", "revTorax", "revPulmones", "revCorazon", "revAbdomen", "revGenitourinario", "revExtremidades", "revMusculo", "revNeurologico", "revPiel", "revEstadoGen"};
      for(String k : srKeys) historia.put(k, "---");
    }

    if (medicalHistory.getVitalSigns() != null) {
      VitalSigns vs = medicalHistory.getVitalSigns();
      historia.put("presionArterial", safeStr.apply(vs.bloodPressure()));
      historia.put("frecuenciaCardiaca", safeStr.apply(vs.heartRate()));
      historia.put("frecuenciaRespiratoria", safeStr.apply(vs.respiratoryRate()));
      historia.put("temperatura", safeStr.apply(vs.temperature()));
      historia.put("saturacionOxigeno", safeStr.apply(vs.oxygenSaturation()));
      historia.put("cintura", safeStr.apply(vs.waistCircumference()));
      historia.put("cadera", safeStr.apply(vs.hipCircumference()));
      historia.put("icc", safeStr.apply(vs.whr()));
      historia.put("peso", safeStr.apply(vs.weight()));
      historia.put("tallaCuadrado", safeStr.apply(vs.heightSquared()));
      historia.put("imc", safeStr.apply(vs.bmi()));
    } else {
      String[] vsKeys = {"presionArterial", "frecuenciaCardiaca", "frecuenciaRespiratoria", "temperatura", "saturacionOxigeno", "cintura", "cadera", "icc", "peso", "tallaCuadrado", "imc"};
      for(String k : vsKeys) historia.put(k, "---");
    }

    context.setVariable("historia", historia);
    String htmlContent = templateEngine.process("medicalHistoryTemplate", context);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    ITextRenderer renderer = new ITextRenderer();

    ClassPathResource fontResource = new ClassPathResource("fonts/Roboto-Regular.ttf");
    File tempFontFile = File.createTempFile("Roboto-Regular", ".ttf");
    tempFontFile.deleteOnExit();

    try (InputStream is = fontResource.getInputStream();
         FileOutputStream os = new FileOutputStream(tempFontFile)) {
      is.transferTo(os);
    }

    renderer.getFontResolver().addFont(tempFontFile.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    renderer.setDocumentFromString(htmlContent);
    renderer.layout();
    renderer.createPDF(outputStream);

    return outputStream.toByteArray();
  }
}
