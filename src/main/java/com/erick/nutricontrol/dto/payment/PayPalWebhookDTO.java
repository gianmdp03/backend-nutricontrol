package com.erick.nutricontrol.dto.payment;

public record PayPalWebhookDTO(
        String event_type,
        Resource resource
) {
    public record Resource(
            String id,
            String status,
            String custom_id,
            Amount amount
    ) {}

    public record Amount(
            String value,
            String currency_code
    ) {}
}