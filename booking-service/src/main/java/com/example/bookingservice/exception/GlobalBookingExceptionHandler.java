package com.example.bookingservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalBookingExceptionHandler {

    @ExceptionHandler(MultiDayBookingException.class)
    public ResponseEntity<ErrorResponse> handleMultiDayBooking(
            MultiDayBookingException ex,
            HttpServletRequest request) {

        return build(
                "MULTIPLE_DAY",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST,
                request);
    }

    @ExceptionHandler(OutsideWorkingHoursException.class)
    public ResponseEntity<ErrorResponse> handleOutsideWorkingHours(
            OutsideWorkingHoursException ex,
            HttpServletRequest request) {

        return build(
                "OUTSIDE_WORKING_HOURS",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST,
                request);
    }

    @ExceptionHandler(TimeSlotUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleTimeSlotUnavailable(
            TimeSlotUnavailableException ex,
            HttpServletRequest request) {

        return build(
                "TIME_SLOT_UNAVAILABLE",
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT,
                request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleBookingValidation(
            Exception ex,
            HttpServletRequest request) {

        return build(
                "BOOKING_VALIDATION_ERROR",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST,
                request);
    }

    @ExceptionHandler(UnauthorizedBookingAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedBookingAccess(
            Exception ex,
            HttpServletRequest request) {

        return build(
                "UNAUTHORIZED_ACCESS",
                ex.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN,
                request);
    }
    @ExceptionHandler(NoBookingsFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoBooking(Exception ex, HttpServletRequest request){
        return build(
                "NO_BOOKING_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND,
                request
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(Exception ex, HttpServletRequest request){
        return build(
                "RESOURCE_NOT_FOUND",
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND,
                request
        );
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        return build(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                request);
    }

    private ResponseEntity<ErrorResponse> build(
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
