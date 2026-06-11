package com.example.bookingservice.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TimeSlotUnavailableException extends RuntimeException{
    private final LocalDateTime conflictStart;
    private final LocalDateTime conflictEnd;

    public TimeSlotUnavailableException(LocalDateTime conflictStart, LocalDateTime conflictEnd) {
        super(String.format("Time slot unavailable. Conflicts with existing booking: %s - %s",
                conflictStart, conflictEnd));
        this.conflictStart = conflictStart;
        this.conflictEnd = conflictEnd;
    }
}
