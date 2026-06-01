package com.example.paymentservice.service;

import com.example.paymentservice.domain.PaymentMethod;
import com.example.paymentservice.model.PaymentOrder;
import com.example.paymentservice.payload.dto.BookingDto;
import com.example.paymentservice.payload.dto.UserDto;
import com.example.paymentservice.payload.response.PaymentLinkResponse;


public interface PaymentService {
	PaymentLinkResponse createOrder(UserDto userDto,
	                                BookingDto bookingDto,
	                                PaymentMethod paymentMethod, String idempotencyKey);
	
	PaymentOrder getPaymentOrderById(Long id);
	
	PaymentOrder getPaymentOrderByPaymentId(String paymentId);
	


	
	boolean confirmPayment(PaymentOrder paymentOrder, String paymentId);
	
	
}
