package com.example.bookingservice.exception;

public class NoBookingsFoundException extends RuntimeException{
    public NoBookingsFoundException(String message){
        super(message);
    }
}
