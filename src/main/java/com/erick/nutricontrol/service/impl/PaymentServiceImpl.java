package com.erick.nutricontrol.service.impl;

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

  @Value("${paypal.return-url}")
  private String returnUrl;

  @Value("${paypal.cancel-url}")
  private String cancelUrl;

  public String createPaymentHold(BigDecimal amount, Long appointmentId)
      throws IOException, ApiException {
    OrdersController ordersController = paypalClient.getOrdersController();

    AmountWithBreakdown amountBreakdown =
        new AmountWithBreakdown.Builder().currencyCode("USD").value(amount.toString()).build();

    PurchaseUnitRequest purchaseUnitRequest =
        new PurchaseUnitRequest.Builder()
            .amount(amountBreakdown)
            .description("Reserva de turno médico #" + appointmentId)
            .build();

    OrderApplicationContext applicationContext =
        new OrderApplicationContext.Builder()
            .returnUrl(returnUrl)
            .cancelUrl(cancelUrl)
            .build();

    OrderRequest orderRequest =
        new OrderRequest.Builder()
            .intent(CheckoutPaymentIntent.AUTHORIZE)
            .purchaseUnits(List.of(purchaseUnitRequest))
            .applicationContext(applicationContext)
            .build();

    CreateOrderInput createOrderInput = new CreateOrderInput.Builder().body(orderRequest).build();

    ApiResponse<Order> apiResponse = ordersController.createOrder(createOrderInput);

    Order order = apiResponse.getResult();

    return order.getId();
  }

  public String confirmPaymentHold(String orderId) throws IOException, ApiException {
    OrdersController ordersController = paypalClient.getOrdersController();

    AuthorizeOrderInput authorizeInput = new AuthorizeOrderInput.Builder().id(orderId).build();

    ApiResponse<OrderAuthorizeResponse> apiResponse =
        ordersController.authorizeOrder(authorizeInput);

    OrderAuthorizeResponse orderAuthorizeResponse = apiResponse.getResult();

    return orderAuthorizeResponse
        .getPurchaseUnits()
        .getFirst()
        .getPayments()
        .getAuthorizations()
        .getFirst()
        .getId();
  }

  public String capturePayment(String authorizationId) throws IOException, ApiException {
    PaymentsController paymentsController = paypalClient.getPaymentsController();

    CaptureAuthorizedPaymentInput captureInput =
        new CaptureAuthorizedPaymentInput.Builder().authorizationId(authorizationId).build();

    ApiResponse<CapturedPayment> apiResponse =
        paymentsController.captureAuthorizedPayment(captureInput);

    CapturedPayment capture = apiResponse.getResult();

    return capture.getId();
  }

  public void voidPayment(String authorizationId) throws IOException, ApiException {
    PaymentsController paymentsController = paypalClient.getPaymentsController();

    VoidPaymentInput voidInput =
        new VoidPaymentInput.Builder().authorizationId(authorizationId).build();

    paymentsController.voidPayment(voidInput);
  }

  public String refundPayment(String captureId) throws IOException, ApiException {
    PaymentsController paymentsController = paypalClient.getPaymentsController();

    RefundCapturedPaymentInput refundInput =
            new RefundCapturedPaymentInput.Builder()
                    .captureId(captureId)
                    .build();

    ApiResponse<Refund> apiResponse =
            paymentsController.refundCapturedPayment(refundInput);

    Refund refund = apiResponse.getResult();

    return refund.getId();
  }
}
