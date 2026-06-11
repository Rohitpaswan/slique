package com.example.bookingservice.exception;

public class MultiDayBookingException extends RuntimeException {
    public MultiDayBookingException(String message
    ) {
        super(message);
    }
}