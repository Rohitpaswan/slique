package com.example.bookingservice.exception;

import lombok.Getter;

import java.util.Set;

@Getter
public class InvalidServiceIdsException extends RuntimeException{
    private final Set<Long> invalidIds;

    public InvalidServiceIdsException(Set<Long> invalidIds) {
        super("Some service IDs don't belong to this booking: " + invalidIds);
        this.invalidIds = invalidIds;
    }

}
