package com.example.paymentservice.service;

import com.example.paymentservice.domain.PaymentMethod;
import com.example.paymentservice.model.PaymentOrder;
import com.example.paymentservice.payload.dto.BookingDto;
import com.example.paymentservice.payload.dto.UserDto;
import com.example.paymentservice.payload.response.PaymentLinkResponse;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayException;

public interface PaymentService {
	PaymentLinkResponse createOrder(UserDto userDto,
	                                BookingDto bookingDto,
	                                PaymentMethod paymentMethod);
	
	PaymentOrder getPaymentOrderById(Long id);
	
	PaymentOrder getPaymentOrderByPaymentId(String paymentId);
	
	PaymentLink createRazorpayPaymentLink(
			UserDto userDto,
			Long amount,
			Long orderId
	) throws RazorpayException;
	
	String createStripePaymentLink(
			UserDto userDto,
			Long amount,
			Long orderId
	);
	
	boolean processedPayment(PaymentOrder paymentOrder, String paymentId, String paymentLinkid);
	
	
}
