package com.example.bookingservice.exception;

import lombok.Getter;

@Getter
public class BookingNotFoundException extends RuntimeException{
    private final Long bookingId;

    public BookingNotFoundException(Long bookingId) {
        super("Booking not found with ID: " + bookingId);
        this.bookingId = bookingId;
    }
}
