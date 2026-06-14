package com.slique.userservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // ── 404 Not Found ─────────────────────────────────────────────────────────
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {
        return buildResponse( "Not_Found", ex.getMessage(), HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND, request);
    }

    // ── 409 Conflict ──────────────────────────────────────────────────────────
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(
            UserAlreadyExistsException ex, HttpServletRequest request) {

        return buildResponse("Conflict", ex.getMessage(), HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT, request);
    }

    // ── 401 Unauthorized ──────────────────────────────────────────────────────
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(
            InvalidTokenException ex, HttpServletRequest request) {

        return buildResponse("Unauthorized",  ex.getMessage(), HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, request);
    }


    // ── 400 Validation ────────────────────────────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        return buildResponse("Validation Failed",
                "One or more fields are invalid",
                HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, request);
    }

    // ── 400 Illegal Argument ──────────────────────────────────────────────────
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {


        return buildResponse("Bad Request", ex.getMessage(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, request);
    }

    // ── 503 Token Generation Failure ──────────────────────────────────────────
    @ExceptionHandler(TokenGenerationException.class)
    public ResponseEntity<ErrorResponse> handleTokenGeneration(
            TokenGenerationException ex, HttpServletRequest request) {

        return buildResponse("TOKEN_GENERATION_FAILED", ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE.value(),
                HttpStatus.SERVICE_UNAVAILABLE, request);
    }

    // ── 500 Fallback ──────────────────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {


        return buildResponse("Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }

    // ── Helper ──────────────────────────────────────
    private ResponseEntity<ErrorResponse> buildResponse(
            String code,
            String message,
            int status,
            HttpStatus httpStatus,
            HttpServletRequest request) {

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
