package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol._enum.AppointmentStatus;
import com.erick.nutricontrol._enum.NotificationType;
import com.erick.nutricontrol._enum.PaymentStatus;
import com.erick.nutricontrol.dto.payment.*;
import com.erick.nutricontrol.exception.ConflictException;
import com.erick.nutricontrol.exception.NotFoundException;
import com.erick.nutricontrol.model.Appointment;
import com.erick.nutricontrol.model.Notification;
import com.erick.nutricontrol.model.Payment;
import com.erick.nutricontrol.repository.AppointmentRepository;
import com.erick.nutricontrol.repository.PaymentRepository;
import com.erick.nutricontrol.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.controllers.PaymentsController;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PaymentServiceImpl implements PaymentService {
  private final PaypalServerSdkClient paypalClient;
  private final PaymentRepository repository;
  private final AppointmentRepository appointmentRepository;
  private final PDFGeneratorService pdfGeneratorService;
  private final EmailService emailService;
  private final GoogleMeetService googleMeetService;
  private final NotificationService notificationService;

  @Value("${nutricontrol.appointments.price}")
  private Integer appointmentPrice;

  @Value("${paypal.return-url}")
  private String returnUrl;

  @Value("${paypal.cancel-url}")
  private String cancelUrl;

  @Value("${paypal.webhook.id}")
  private String webhookId;

  @Value("${paypal.mode}")
  private String mode;

  @Override
  @Transactional
  public PaymentOrderResponseDTO createPaymentHold(PaymentRequestDTO paymentRequestDTO)
      throws ApiException, IOException {
    Appointment appointment =
        appointmentRepository
            .findById(paymentRequestDTO.appointmentId())
            .orElseThrow(() -> new NotFoundException("Appointment not found"));
    BigDecimal amount = BigDecimal.valueOf(this.appointmentPrice);

    OrdersController ordersController = paypalClient.getOrdersController();

    AmountWithBreakdown amountBreakdown =
        new AmountWithBreakdown.Builder().currencyCode("USD").value(amount.toString()).build();

    PurchaseUnitRequest purchaseUnitRequest =
        new PurchaseUnitRequest.Builder()
            .amount(amountBreakdown)
            .description("Reserva de turno médico #" + appointment.getId())
            .customId(appointment.getId().toString())
            .build();

    OrderApplicationContext applicationContext =
        new OrderApplicationContext.Builder().returnUrl(returnUrl).cancelUrl(cancelUrl).build();

    OrderRequest orderRequest =
        new OrderRequest.Builder()
            .intent(CheckoutPaymentIntent.AUTHORIZE)
            .purchaseUnits(List.of(purchaseUnitRequest))
            .applicationContext(applicationContext)
            .build();

    CreateOrderInput createOrderInput = new CreateOrderInput.Builder().body(orderRequest).build();

    ApiResponse<Order> apiResponse = ordersController.createOrder(createOrderInput);
    Order order = apiResponse.getResult();

    String approveLink =
        order.getLinks().stream()
            .filter(link -> "approve".equals(link.getRel()))
            .findFirst()
            .map(LinkDescription::getHref)
            .orElseThrow(() -> new NotFoundException("Payment link not found"));

    Payment payment =
        Payment.builder()
            .appointment(appointment)
            .amount(amount)
            .currency("USD")
            .paypalOrderId(order.getId())
            .status(PaymentStatus.PENDING)
            .build();
    repository.save(payment);
    Notification notification =
        Notification.builder()
            .type(NotificationType.ADMIN_APPOINTMENT_CONFIRMED)
            .message("Un usuario sacó un nuevo turno")
            .build();
    notificationService.createNotification(appointment.getAdmin(), notification);
    return new PaymentOrderResponseDTO(order.getId(), approveLink);
  }

  @Override
  @Transactional
  public void confirmPaymentHold(PaymentConfirmRequestDTO confirmDTO) throws Exception {
    Payment payment =
        repository
            .findByPaypalOrderId(confirmDTO.paypalOrderId())
            .orElseThrow(() -> new NotFoundException("Payment not found"));
    Appointment appointment = payment.getAppointment();
    OrdersController ordersController = paypalClient.getOrdersController();

    AuthorizeOrderInput authorizeInput =
        new AuthorizeOrderInput.Builder().id(confirmDTO.paypalOrderId()).build();

    ApiResponse<OrderAuthorizeResponse> apiResponse =
        ordersController.authorizeOrder(authorizeInput);

    OrderAuthorizeResponse orderAuthorizeResponse = apiResponse.getResult();

    String authorizationId =
        orderAuthorizeResponse
            .getPurchaseUnits()
            .getFirst()
            .getPayments()
            .getAuthorizations()
            .getFirst()
            .getId();

    payment.setPaypalAuthorizationId(authorizationId);
    payment.setStatus(PaymentStatus.AUTHORIZED);
    repository.save(payment);

    if (appointment.getAppointmentStatus().equals(AppointmentStatus.CANCELLED)) {
      this.voidPayment(authorizationId);
      throw new ConflictException(
          "El tiempo para pagar expiró y el turno fue liberado. Hemos anulado la retención y los fondos no se cobrarán.");
    }

    OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);
    if (appointment.getStartTimeUtc().isBefore(nowUtc.plusHours(24))) {
      log.info(
          "El turno (ID: {}) es en menos de 24hs. Capturando fondos inmediatamente.",
          appointment.getId());
      this.capturePaymentAsync(authorizationId, appointment.getId());
    }

    appointment.setAppointmentStatus(AppointmentStatus.CONFIRMED);
    String meetLink = googleMeetService.createMeetLink(appointment);
    if (meetLink != null) {
      appointment.setMeetingLink(meetLink);
    }
    appointmentRepository.save(appointment);

    String patientName = appointment.getUser().getName();
    String patientEmail = appointment.getUser().getEmail();
    String appointmentDate = appointment.getDate().toString();
    String appointmentTime = appointment.getStartTime().toString();
    String doctorName = appointment.getAdmin().getName();

    emailService.sendAppointmentReceiptAsync(
        patientEmail,
        patientName,
        appointmentDate,
        appointmentTime,
        doctorName,
        appointment.getMeetingLink());
  }

  @Override
  @Transactional
  public String capturePayment(String authorizationId) throws IOException, ApiException {
    Payment payment =
        repository
            .findByPaypalAuthorizationId(authorizationId)
            .orElseThrow(() -> new NotFoundException("Payment not found"));
    PaymentsController paymentsController = paypalClient.getPaymentsController();

    CaptureAuthorizedPaymentInput captureInput =
        new CaptureAuthorizedPaymentInput.Builder().authorizationId(authorizationId).build();

    ApiResponse<CapturedPayment> apiResponse =
        paymentsController.captureAuthorizedPayment(captureInput);

    CapturedPayment capture = apiResponse.getResult();

    payment.setPaypalCaptureId(capture.getId());
    payment.setStatus(PaymentStatus.CAPTURED);
    repository.save(payment);

    return capture.getId();
  }

  @Override
  @Transactional
  @Async
  public void capturePaymentAsync(String authorizationId, Long appointmentId) {
    try {
      capturePayment(authorizationId);
      log.info("Pago capturado asíncronamente para el turno {}", appointmentId);
    } catch (Exception e) {
      log.error(
          "Fallo al intentar capturar automáticamente el pago del turno ID: {}", appointmentId, e);

      repository
          .findByPaypalAuthorizationId(authorizationId)
          .ifPresent(
              payment -> {
                payment.setStatus(PaymentStatus.FAILED);
                repository.save(payment);
                log.info("Estado del pago actualizado a FAILED para el turno {}", appointmentId);
              });
    }
  }

  @Override
  @Transactional
  public void voidPayment(String authorizationId) throws IOException, ApiException {
    Payment payment =
        repository
            .findByPaypalAuthorizationId(authorizationId)
            .orElseThrow(() -> new NotFoundException("Payment not found"));
    PaymentsController paymentsController = paypalClient.getPaymentsController();

    VoidPaymentInput voidInput =
        new VoidPaymentInput.Builder().authorizationId(authorizationId).build();

    paymentsController.voidPayment(voidInput);

    payment.setStatus(PaymentStatus.VOIDED);
    repository.save(payment);
  }

  @Override
  @Transactional
  public String refundPayment(String captureId) throws IOException, ApiException {
    Payment payment =
        repository
            .findByPaypalCaptureId(captureId)
            .orElseThrow(() -> new NotFoundException("Payment not found"));

    PaymentsController paymentsController = paypalClient.getPaymentsController();

    RefundCapturedPaymentInput refundInput =
        new RefundCapturedPaymentInput.Builder().captureId(captureId).build();

    ApiResponse<Refund> apiResponse = paymentsController.refundCapturedPayment(refundInput);

    Refund refund = apiResponse.getResult();

    payment.setPaypalRefundId(refund.getId());
    payment.setStatus(PaymentStatus.REFUNDED);
    repository.save(payment);

    return refund.getId();
  }

  @Override
  @Async
  @Transactional
  public void processWebhook(PayPalWebhookDTO payload) {
    try {
      log.info(
          "Procesando webhook de PayPal en background para el evento: {}", payload.event_type());

      String eventType = payload.event_type();
      String appointmentIdStr = payload.resource().custom_id();

      if (appointmentIdStr == null) {
        log.warn("El webhook no contiene custom_id. Se ignora.");
        return;
      }

      Long appointmentId = Long.parseLong(appointmentIdStr);
      Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
      if (appointment == null) {
        log.warn("Turno no encontrado para el custom_id: {}", appointmentId);
        return;
      }

      if ("PAYMENT.AUTHORIZATION.CREATED".equals(eventType)) {
        appointment.getPayments().stream()
            .filter(p -> p.getStatus() == PaymentStatus.PENDING)
            .findFirst()
            .ifPresent(
                p -> {
                  p.setPaypalAuthorizationId(payload.resource().id());
                  p.setStatus(PaymentStatus.AUTHORIZED);
                  repository.save(p);
                  log.info("Pago autorizado (Webhook) para el turno: {}", appointmentId);
                });

        appointment.setAppointmentStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appointment);

      } else if ("PAYMENT.CAPTURE.COMPLETED".equals(eventType)) {
        appointment.getPayments().stream()
            .filter(
                p ->
                    p.getStatus() == PaymentStatus.AUTHORIZED
                        || p.getStatus() == PaymentStatus.PENDING)
            .findFirst()
            .ifPresent(
                p -> {
                  p.setPaypalCaptureId(payload.resource().id());
                  p.setStatus(PaymentStatus.CAPTURED);
                  repository.save(p);
                  log.info("Pago capturado (Webhook) para el turno: {}", appointmentId);
                });

        appointmentRepository.save(appointment);
      }

    } catch (Exception e) {
      log.error("Fallo inesperado al procesar el webhook de PayPal en background", e);
    }
  }

  @Override
  public boolean verifyWebhookSignature(
      String authAlgo,
      String certUrl,
      String transmissionId,
      String transmissionSig,
      String transmissionTime,
      String rawPayload)
      throws Exception {

    String baseUrl =
        mode.equalsIgnoreCase("sandbox")
            ? "https://api-m.sandbox.paypal.com"
            : "https://api-m.paypal.com";
    String url = baseUrl + "/v1/notifications/verify-webhook-signature";

    Map<String, Object> body = new HashMap<>();
    body.put("auth_algo", authAlgo);
    body.put("cert_url", certUrl);
    body.put("transmission_id", transmissionId);
    body.put("transmission_sig", transmissionSig);
    body.put("transmission_time", transmissionTime);
    body.put("webhook_id", webhookId);

    ObjectMapper mapper = new ObjectMapper();
    body.put("webhook_event", mapper.readValue(rawPayload, Object.class));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
    RestTemplate restTemplate = new RestTemplate();

    try {
      ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
      Map<String, String> responseBody = response.getBody();

      return responseBody != null && "SUCCESS".equals(responseBody.get("verification_status"));

    } catch (Exception e) {
      throw new Exception("Error comunicándose con el validador de PayPal", e);
    }
  }

  @Override
  @Async
  @Transactional
  public void processRefundOrVoidAsync(
      String authorizationId, String captureId, boolean isAuthorized) {
    try {
      if (isAuthorized && authorizationId != null) {
        voidPayment(authorizationId);
      } else if (!isAuthorized && captureId != null) {
        refundPayment(captureId);
      }
    } catch (Exception e) {
      log.error("Error asíncrono en PayPal al procesar reembolso/anulación", e);
    }
  }

  @Override
  @Async
  @Transactional
  public void forcePenaltyCaptureAsync(String authorizationId) {
    try {
      if (authorizationId != null) {
        capturePayment(authorizationId);
      }
    } catch (Exception e) {
      log.error("Error asíncrono en PayPal al cobrar penalidad", e);
    }
  }
}
