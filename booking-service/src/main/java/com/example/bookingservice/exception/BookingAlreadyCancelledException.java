package com.example.bookingservice.exception;

import lombok.Getter;

@Getter
public class BookingAlreadyCancelledException extends RuntimeException{

    private final Long bookingId;

    public BookingAlreadyCancelledException(Long bookingId) {
        super("Cannot modify an already cancelled booking with ID: " + bookingId);
        this.bookingId = bookingId;
    }

}
