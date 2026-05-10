package com.erick.nutricontrol.controller;

import com.erick.nutricontrol.dto.payment.* ;
import com.erick.nutricontrol.exception.BadRequestException;
import com.erick.nutricontrol.service.PaymentService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/hold")
    public ResponseEntity<PaymentOrderResponseDTO> createHold(@Valid @RequestBody PaymentRequestDTO requestDTO) {
        try {
            PaymentOrderResponseDTO response = paymentService.createPaymentHold(requestDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Idealmente, esto lo ataja tu GlobalExceptionHandler
            throw new BadRequestException("Error al comunicarse con PayPal: " + e.getMessage());
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmHold(@Valid @RequestBody PaymentConfirmRequestDTO confirmDTO) {
        try {
            paymentService.confirmPaymentHold(confirmDTO);
            return ResponseEntity.ok("Pago retenido con éxito en PayPal y guardado en la base de datos.");
        } catch (Exception e) {
            throw new BadRequestException("Error al confirmar el pago en PayPal: " + e.getMessage());
        }
    }

    @PostMapping("/capture")
    public ResponseEntity<String> capturePayment(@Valid @RequestBody PaymentCaptureRequestDTO requestDTO) {
        try {
            String captureId = paymentService.capturePayment(requestDTO.authorizationId());
            return ResponseEntity.ok("Pago cobrado con éxito. ID de transacción: " + captureId);
        } catch (Exception e) {
            throw new BadRequestException("Error al efectuar el cobro: " + e.getMessage());
        }
    }

    @PostMapping("/void")
    public ResponseEntity<String> voidPayment(@Valid @RequestBody PaymentVoidRequestDTO requestDTO) {
        try {
            paymentService.voidPayment(requestDTO.authorizationId());
            return ResponseEntity.ok("Retención anulada. Los fondos fueron liberados sin costo.");
        } catch (Exception e) {
            throw new BadRequestException("Error al anular la retención: " + e.getMessage());
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handlePayPalWebhook(
            @RequestHeader("paypal-auth-algo") String authAlgo,
            @RequestHeader("paypal-cert-url") String certUrl,
            @RequestHeader("paypal-transmission-id") String transmissionId,
            @RequestHeader("paypal-transmission-sig") String transmissionSig,
            @RequestHeader("paypal-transmission-time") String transmissionTime,
            @RequestBody String rawPayload) {

        log.info("Webhook recibido. Validando firma de seguridad...");

        try {
            boolean isValid = paymentService.verifyWebhookSignature(
                    authAlgo, certUrl, transmissionId, transmissionSig, transmissionTime, rawPayload
            );

            if (!isValid) {
                System.err.println("ALERTA: Firma de webhook inválida. Ignorando petición.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            PayPalWebhookDTO payload = mapper.readValue(rawPayload, PayPalWebhookDTO.class);

            paymentService.processWebhook(payload);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            System.err.println("Error al procesar el webhook: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/refund")
    public ResponseEntity<String> refundPayment(@Valid @RequestBody PaymentRefundRequestDTO requestDTO) {
        try {
            String refundId = paymentService.refundPayment(requestDTO.captureId());
            return ResponseEntity.ok("Reembolso procesado con éxito. ID de reembolso: " + refundId);
        } catch (Exception e) {
            throw new BadRequestException("Error al procesar el reembolso: " + e.getMessage());
        }
    }
}