package com.example.bookingservice.service.client;


import com.example.bookingservice.dto.BookingDto;
import com.example.bookingservice.dto.PaymentLinkResponse;
import com.example.bookingservice.model.PaymentMethod;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment", path = "/api/payments")
public interface PaymentFeignClient {

    @PostMapping
    ResponseEntity<PaymentLinkResponse> createPaymentLink(@RequestBody BookingDto bookingDto,
                                                      @RequestParam PaymentMethod paymentMethod, @RequestHeader("Authorization") String jwt, @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey);
}
