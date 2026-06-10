package com.example.paymentservice.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;



import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@Builder
public class ErrorResponse {
    private  String errorCode;
    private String message;
    private String path;
    private int statusCode;
    private LocalDateTime timestamp;
}
