package com.example.bookingservice.exception;

public class UnauthorizedBookingAccessException extends RuntimeException{
    public UnauthorizedBookingAccessException(String message) {
        super(message);
    }
}
