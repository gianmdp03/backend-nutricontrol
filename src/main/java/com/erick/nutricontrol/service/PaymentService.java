package com.erick.nutricontrol.service;

import com.erick.nutricontrol.dto.payment.PaymentConfirmRequestDTO;
import com.erick.nutricontrol.dto.payment.PaymentOrderResponseDTO;
import com.erick.nutricontrol.dto.payment.PaymentRequestDTO;
import com.paypal.sdk.exceptions.ApiException;

import java.io.IOException;

public interface PaymentService {
    PaymentOrderResponseDTO createPaymentHold(PaymentRequestDTO paymentRequestDTO) throws IOException, ApiException;
    void confirmPaymentHold(PaymentConfirmRequestDTO confirmDTO) throws Exception;
    String capturePayment(String authorizationId) throws IOException, ApiException;
    void voidPayment(String authorizationId) throws IOException, ApiException;
    String refundPayment(String captureId) throws IOException, ApiException;
}
