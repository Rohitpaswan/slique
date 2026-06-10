package com.example.paymentservice.exception;

import com.example.paymentservice.exception.payment.*;
import com.example.paymentservice.exception.webhook.WebhookDuplicateEventException;
import com.example.paymentservice.exception.webhook.WebhookOrderNotFoundException;
import com.example.paymentservice.exception.webhook.WebhookProcessingException;
import com.example.paymentservice.exception.webhook.WebhookSignatureException;
import com.example.paymentservice.payload.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalPaymentExceptionHandler {
    @ExceptionHandler(MissingIdempotencyKeyException.class)
    public ResponseEntity<ErrorResponse> handleMissingKey(MissingIdempotencyKeyException ex, HttpServletRequest request) {
        return build("BAD_REQUEST", ex.getMessage(), 400,
                HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(PaymentOrderBrokenException.class)
    public ResponseEntity<ErrorResponse> handleBrokenOrder(PaymentOrderBrokenException ex, HttpServletRequest request) {
        return build("PAYMENT_ORDER_BROKEN", ex.getMessage(), 409,
                HttpStatus.CONFLICT, request);

    }

    @ExceptionHandler(PaymentOrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleOrderNotFound(
            PaymentOrderNotFoundException ex, HttpServletRequest request) {
        return build("PAYMENT_ORDER_NOT_FOUND", ex.getMessage(), 404,
                HttpStatus.NOT_FOUND, request);
    }


    @ExceptionHandler(PaymentGatewayException.class)
    public ResponseEntity<ErrorResponse> handleGatewayFailure(PaymentGatewayException ex, HttpServletRequest request) {
        return build("PAYMENT_GATEWAY_FAILED", ex.getMessage(), 503,
                HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    @ExceptionHandler(PaymentVerificationException.class)
    public ResponseEntity<ErrorResponse> handleVerificationFailed(PaymentVerificationException ex, HttpServletRequest request) {
        return build("PAYMENT_VERIFICATION_FAILED", ex.getMessage(), 400,
                HttpStatus.BAD_REQUEST, request);
    }

    //webhook
    @ExceptionHandler(WebhookSignatureException.class)
    public ResponseEntity<ErrorResponse> handleWebhookSignature(
            WebhookSignatureException ex, HttpServletRequest request) {
        return build("WEBHOOK_SIGNATURE_INVALID", ex.getMessage(), 401,
                HttpStatus.UNAUTHORIZED, request);
    }


    @ExceptionHandler(WebhookDuplicateEventException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEvent(WebhookDuplicateEventException ex, HttpServletRequest request) {
        return build("WEBHOOK_DUPLICATE_EVENT", ex.getMessage(), 409,
                HttpStatus.CONFLICT, request);
    }

    @ExceptionHandler(WebhookOrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWebhookOrderNotFound(WebhookOrderNotFoundException ex, HttpServletRequest request) {
        return build("WEBHOOK_ORDER_NOT_FOUND", ex.getMessage(), 404,
                HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(WebhookProcessingException.class)
    public ResponseEntity<ErrorResponse> handleWebhookProcessing(WebhookProcessingException ex, HttpServletRequest request) {
        return build("WEBHOOK_PROCESSING_FAILED", ex.getMessage(), 500,
                HttpStatus.INTERNAL_SERVER_ERROR, request);
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest request) {
        return build("INTERNAL_ERROR", "An unexpected error occurred", 500,
                HttpStatus.INTERNAL_SERVER_ERROR, request);
    }


    private ResponseEntity<ErrorResponse> build(
            String code, String message, int status,
            HttpStatus httpStatus, HttpServletRequest request) {

        ErrorResponse body = ErrorResponse.builder()
                .errorCode(code)
                .message(message)
                .path(request.getRequestURI())
                .statusCode(status)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(httpStatus).body(body);
    }
}
