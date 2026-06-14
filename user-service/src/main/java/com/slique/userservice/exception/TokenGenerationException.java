package com.slique.userservice.exception;


public class TokenGenerationException extends RuntimeException{
    public TokenGenerationException(String message){
        super(message);
    }
}