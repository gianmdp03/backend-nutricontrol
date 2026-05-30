package com.erick.nutricontrol.model;

import com.erick.nutricontrol._enum.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payment_appointment", columnList = "appointment_id"),
        @Index(name = "idx_payment_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(length = 3, nullable = false)
    private String currency;

    @Column(name = "paypal_order_id", unique = true)
    private String paypalOrderId;

    @Column(name = "paypal_authorization_id", unique = true)
    private String paypalAuthorizationId;

    @Column(name = "paypal_capture_id", unique = true)
    private String paypalCaptureId;

    @Column(name = "paypal_refund_id", unique = true)
    private String paypalRefundId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Builder
    public Payment(Appointment appointment, BigDecimal amount, String currency, String paypalOrderId, String paypalAuthorizationId, String paypalCaptureId, String paypalRefundId, PaymentStatus status) {
        this.appointment = appointment;
        this.amount = amount;
        this.currency = currency;
        this.paypalOrderId = paypalOrderId;
        this.paypalAuthorizationId = paypalAuthorizationId;
        this.paypalCaptureId = paypalCaptureId;
        this.paypalRefundId = paypalRefundId;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Payment payment)) return false;
        return id != null && id.equals(payment.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now(ZoneOffset.UTC);
        this.updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}