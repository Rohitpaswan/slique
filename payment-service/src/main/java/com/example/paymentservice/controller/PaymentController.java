package com.example.paymentservice.controller;


import com.example.paymentservice.domain.PaymentMethod;
import com.example.paymentservice.model.PaymentOrder;
import com.example.paymentservice.payload.dto.BookingDto;
import com.example.paymentservice.payload.dto.UserDto;
import com.example.paymentservice.payload.response.PaymentLinkResponse;
import com.example.paymentservice.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments/")
@AllArgsConstructor

public class PaymentController {
	private final PaymentService paymentService;
	
	@PostMapping
	public ResponseEntity<PaymentLinkResponse> createPayment(@RequestBody BookingDto bookingDto,
	                                                         @RequestBody PaymentMethod paymentMethod) {
		UserDto userDto = new UserDto();
		userDto.setFullName("Hrithik");
		userDto.setEmail("hrx1234");
		userDto.setId(1L);
		
		PaymentLinkResponse paymentLinkResponse = paymentService.createOrder(userDto, bookingDto, paymentMethod);
		return ResponseEntity.status(HttpStatus.CREATED).body(paymentLinkResponse);
		
	}
	
	
	@GetMapping("/{paymentOrderId}")
	
	public ResponseEntity<PaymentOrder> getPaymentOrderById(@PathVariable Long paymentOrderId) {
		UserDto userDto = new UserDto();
		userDto.setFullName("Hrithik");
		userDto.setEmail("hrx1234");
		userDto.setId(1L);
		
		PaymentOrder paymentOrder = paymentService.getPaymentOrderById(paymentOrderId);
		return ResponseEntity.status(HttpStatus.OK).body(paymentOrder);
		
	}
	
	@GetMapping("/proceed")
	
	public ResponseEntity<Boolean> processedPayment(@RequestParam String paymentId,
	                                                @RequestParam String paymentLinkid) {
		
		PaymentOrder paymentOrder = paymentService.getPaymentOrderByPaymentId(paymentId);
		Boolean res = paymentService.processedPayment(paymentOrder, paymentId, paymentLinkid);
		return ResponseEntity.ok(res);
		
	}
	
	
}
