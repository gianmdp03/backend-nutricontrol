package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
  private final JavaMailSender javaMailSender;
  private final TemplateEngine templateEngine;

  @Value("${spring.mail.username}")
  private String email;

  @Override
  @Async
  public void sendEmail(String to, String subject, String body) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(email);
    message.setTo(to);
    message.setSubject(subject);
    message.setText(body);

    javaMailSender.send(message);
  }

  @Override
  @Async
  public void sendEmailWithReceipt(String to, String subject, String body, byte[] pdfBytes)
      throws Exception {
    MimeMessage message = javaMailSender.createMimeMessage();

    MimeMessageHelper helper = new MimeMessageHelper(message, true);

    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(body);

    helper.addAttachment("Comprobante_Turno.pdf", new ByteArrayResource(pdfBytes));

    javaMailSender.send(message);
  }

  @Override
  @Async
  public void sendHtmlTemplateEmail(
      String to, String subject, String templateName, Map<String, Object> variables) {
    try {
      Context context = new Context();
      context.setVariables(variables);

      String htmlContent = templateEngine.process(templateName, context);

      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

      helper.setFrom(email);
      helper.setTo(to);
      helper.setSubject(subject);

      //HTML = TRUE
      helper.setText(htmlContent, true);

      javaMailSender.send(message);
      log.info("Email HTML enviado exitosamente a: {}", to);
    } catch (Exception e) {
      log.error("Fallo al enviar el correo HTML basado en plantilla a " + to, e);
    }
  }
}
