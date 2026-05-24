package com.example.bookingservice.request;

import lombok.Data;

import java.util.Set;

@Data
public class BookingUpdateRequest {
    private Set<Long> cancelServiceIds;
}
