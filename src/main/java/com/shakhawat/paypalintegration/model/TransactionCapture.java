package com.shakhawat.paypalintegration.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "paypal_capture_transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCapture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "capture_id", unique = true, nullable = false)
    private String captureId;

    @Column(name = "status")
    private String status;

    @Column(name = "amount", precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "is_final_capture")
    private Boolean finalCapture;

    @Column(name = "create_time")
    private OffsetDateTime createTime;

    @Column(name = "update_time")
    private OffsetDateTime updateTime;

    @Column(name = "invoice_id")
    private String invoiceId;

    @Column(name = "custom_id")
    private String customId;

    @Column(name = "raw_payload", columnDefinition = "TEXT")
    private String rawPayload;
}

