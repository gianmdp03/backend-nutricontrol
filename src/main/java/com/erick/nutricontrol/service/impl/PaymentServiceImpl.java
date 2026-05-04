package com.erick.nutricontrol.service.impl;

import com.erick.nutricontrol._enum.PaymentStatus;
import com.erick.nutricontrol.dto.payment.PaymentConfirmRequestDTO;
import com.erick.nutricontrol.dto.payment.PaymentOrderResponseDTO;
import com.erick.nutricontrol.dto.payment.PaymentRequestDTO;
import com.erick.nutricontrol.exception.NotFoundException;
import com.erick.nutricontrol.model.Appointment;
import com.erick.nutricontrol.model.Payment;
import com.erick.nutricontrol.repository.AppointmentRepository;
import com.erick.nutricontrol.repository.PaymentRepository;
import com.erick.nutricontrol.service.PaymentService;
import com.paypal.sdk.PaypalServerSdkClient;
import com.paypal.sdk.controllers.OrdersController;
import com.paypal.sdk.controllers.PaymentsController;
import com.paypal.sdk.exceptions.ApiException;
import com.paypal.sdk.http.response.ApiResponse;
import com.paypal.sdk.models.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {
  private final PaypalServerSdkClient paypalClient;
  private final PaymentRepository repository;
  private final AppointmentRepository appointmentRepository;

  @Value("${paypal.return-url}")
  private String returnUrl;

  @Value("${paypal.cancel-url}")
  private String cancelUrl;

  @Transactional
  public PaymentOrderResponseDTO createPaymentHold(PaymentRequestDTO paymentRequestDTO)
      throws ApiException, IOException {
    Appointment appointment =
        appointmentRepository
            .findById(paymentRequestDTO.appointmentId())
            .orElseThrow(() -> new NotFoundException("Appointment not found"));
    BigDecimal amount = BigDecimal.valueOf(150);

    OrdersController ordersController = paypalClient.getOrdersController();

    AmountWithBreakdown amountBreakdown =
        new AmountWithBreakdown.Builder().currencyCode("USD").value(amount.toString()).build();

    PurchaseUnitRequest purchaseUnitRequest =
        new PurchaseUnitRequest.Builder()
            .amount(amountBreakdown)
            .description("Reserva de turno médico #" + appointment.getId())
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

    Payment payment = Payment.builder()
            .appointment(appointment)
            .amount(amount)
            .currency("USD")
            .paypalOrderId(order.getId())
            .status(PaymentStatus.PENDING)
            .build();
    repository.save(payment);

    return new PaymentOrderResponseDTO(order.getId(), approveLink);
  }

  @Transactional
  public void confirmPaymentHold(PaymentConfirmRequestDTO confirmDTO) throws IOException, ApiException {
    Payment payment = repository.findByPaypalOrderId(confirmDTO.paypalOrderId()).orElseThrow(() -> new NotFoundException("Payment not found"));

    OrdersController ordersController = paypalClient.getOrdersController();

    AuthorizeOrderInput authorizeInput =
        new AuthorizeOrderInput.Builder().id(confirmDTO.paypalOrderId()).build();

    ApiResponse<OrderAuthorizeResponse> apiResponse =
        ordersController.authorizeOrder(authorizeInput);

    OrderAuthorizeResponse orderAuthorizeResponse = apiResponse.getResult();

    String authorizationId = orderAuthorizeResponse
        .getPurchaseUnits()
        .getFirst()
        .getPayments()
        .getAuthorizations()
        .getFirst()
        .getId();

    payment.setPaypalAuthorizationId(authorizationId);
    payment.setStatus(PaymentStatus.AUTHORIZED);
    repository.save(payment);
  }

  @Transactional
  public String capturePayment(String authorizationId) throws IOException, ApiException {
    Payment payment = repository.findByPaypalAuthorizationId(authorizationId).orElseThrow(() -> new NotFoundException("Payment not found"));
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

  @Transactional
  public void voidPayment(String authorizationId) throws IOException, ApiException {
    Payment payment = repository.findByPaypalAuthorizationId(authorizationId).orElseThrow(() -> new NotFoundException("Payment not found"));
    PaymentsController paymentsController = paypalClient.getPaymentsController();

    VoidPaymentInput voidInput =
        new VoidPaymentInput.Builder().authorizationId(authorizationId).build();

    paymentsController.voidPayment(voidInput);

    payment.setStatus(PaymentStatus.VOIDED);
    repository.save(payment);
  }

  @Transactional
  public String refundPayment(String captureId) throws IOException, ApiException {
    Payment payment = repository.findByPaypalCaptureId(captureId).orElseThrow(() -> new NotFoundException("Payment not found"));

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
}
