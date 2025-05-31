package com.shakhawat.paypalintegration.exception;

import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {

    private final String paymentId;
    private final String errorCode;

    public PaymentException(String message) {
        super(message);
        this.paymentId = null;
        this.errorCode = null;
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
        this.paymentId = null;
        this.errorCode = null;
    }

    public PaymentException(String message, String paymentId) {
        super(message);
        this.paymentId = paymentId;
        this.errorCode = null;
    }

    public PaymentException(String message, String paymentId, String errorCode) {
        super(message);
        this.paymentId = paymentId;
        this.errorCode = errorCode;
    }

    public PaymentException(String message, String paymentId, Throwable cause) {
        super(message, cause);
        this.paymentId = paymentId;
        this.errorCode = null;
    }

    public PaymentException(String message, String paymentId, String errorCode, Throwable cause) {
        super(message, cause);
        this.paymentId = paymentId;
        this.errorCode = errorCode;
    }
}
