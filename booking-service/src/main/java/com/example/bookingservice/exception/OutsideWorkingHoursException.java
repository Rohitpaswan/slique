package com.example.bookingservice.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class OutsideWorkingHoursException extends RuntimeException{
    private final LocalDateTime openingTime;
    private final LocalDateTime closingTime;

    public OutsideWorkingHoursException(LocalDateTime openingTime, LocalDateTime closingTime) {
        super(String.format("Booking must be within working hours: %s - %s", openingTime, closingTime));
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

}
