package com.example.paymentservice.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequest {
    private String customerName;
    private String customerEmail;
    private Long amount;
    private String currency;
    private Long paymentId;
    private Long orderId;
    private Long bookingId;
    private Long userId;
    private Long salonId;

}
