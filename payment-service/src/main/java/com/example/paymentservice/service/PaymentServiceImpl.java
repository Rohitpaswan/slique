package com.example.paymentservice.service;


import com.example.paymentservice.domain.PaymentMethod;
import com.example.paymentservice.domain.PaymentOrderStatus;
import com.example.paymentservice.model.PaymentOrder;
import com.example.paymentservice.payload.dto.BookingDto;
import com.example.paymentservice.payload.dto.UserDto;
import com.example.paymentservice.payload.response.PaymentLinkResponse;
import com.example.paymentservice.repository.PaymentOrderRepository;
import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
	private final PaymentOrderRepository paymentOrderRepository;
	private final RazorpayClient razorpayClient;
	
	
	@Override
	public PaymentLinkResponse createOrder(UserDto userDto, BookingDto bookingDto, PaymentMethod paymentMethod) {
		BigDecimal totalPrice = bookingDto.getTotalPrice();
		Long amount = totalPrice
				.multiply(BigDecimal.valueOf(100))
				.longValue();
		
		PaymentOrder paymentOrder = new PaymentOrder();
		paymentOrder.setPaymentMethod(paymentMethod);
		paymentOrder.setBookingId(bookingDto.getId());
		paymentOrder.setAmount(amount);
		PaymentOrder saveOrder = paymentOrderRepository.save(paymentOrder);
		PaymentLinkResponse paymentLinkResponse = new PaymentLinkResponse();
		
		if (PaymentMethod.RAZORPAY.equals(paymentMethod)) {
			
			try {
				
				PaymentLink paymentLink = createRazorpayPaymentLink(userDto, saveOrder.getAmount(), saveOrder.getId());
				
				String paymentUrl = paymentLink.get("short_url");
				String paymentUrlId = paymentLink.get("id");
				paymentLinkResponse.setPayment_link_url(paymentUrl);
				paymentLinkResponse.setGetPayment_link_id(paymentUrlId);
				
				paymentOrderRepository.save(paymentOrder);
			} catch (RazorpayException e) {
				throw new RuntimeException(e);
			}
			
		}
		
		else if(PaymentMethod.STRIPE.equals(paymentMethod)){
			String paymentLink = createStripePaymentLink(userDto, saveOrder.getAmount(), saveOrder.getId());
			paymentOrder.setPaymentLink(paymentLink);
			paymentOrderRepository.save(paymentOrder);
			
		}
		
		return paymentLinkResponse;
	}
	
	@Override
	public PaymentOrder getPaymentOrderById(Long id) {
		return paymentOrderRepository.findById(id).orElseThrow(() -> new RuntimeException("Payment order not found" + id));
	}
	
	@Override
	public PaymentOrder getPaymentOrderByPaymentId(String paymentId) {
		return null;
	}
	
	@Override
	public PaymentLink createRazorpayPaymentLink(UserDto userDto, Long amount, Long orderId) throws RazorpayException {
		
		JSONObject paymentLinkRequest = new JSONObject();
		paymentLinkRequest.put("amount", amount);
		paymentLinkRequest.put("currency", "INR");
		paymentLinkRequest.put("description", "Payment for booking " + orderId);
		
		JSONObject customer = new JSONObject();
		customer.put("name", userDto.getFullName());
		customer.put("email", userDto.getEmail());
		
		paymentLinkRequest.put("customer", customer);
		JSONObject notify = new JSONObject();
		notify.put("email", true);
		
		paymentLinkRequest.put("notify", notify);
		paymentLinkRequest.put("reminder_enable", true);
		paymentLinkRequest.put("callback_url", "http://localhost:3000/payment-success/" + orderId);
		paymentLinkRequest.put("callback_method", "get");
		
		return razorpayClient.paymentLink.create(paymentLinkRequest);
		
		
	}
	
	@Override
	public String createStripePaymentLink(UserDto userDto, Long amount, Long orderId) {
		try {
			Session session = Session.create(buildSessionParam(amount, orderId));
			return session.getUrl();
			
		} catch (Exception e) {
			throw new RuntimeException("Error creating Stripe session", e);
		}
	}
	
	@Override
	public boolean processedPayment(PaymentOrder paymentOrder, String paymentId, String paymentLinkid) {
		
		if(PaymentOrderStatus.PENDING.equals(paymentOrder.getStatus())){
				if(PaymentMethod.RAZORPAY.equals(paymentOrder.getPaymentMethod())){
					try {
						Payment payment = razorpayClient.payments.fetch(paymentId);
						String status = payment.get("status");
						if(status.equals("captured")){
							paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
							paymentOrderRepository.save(paymentOrder);
							return true;
						}
					} catch (RazorpayException e) {
						throw new RuntimeException(e);
					}
				}
		}
		
		
		else{
			paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
			paymentOrderRepository.save(paymentOrder);
			return true;
		}
		return false;
	}
	
	
	private SessionCreateParams buildSessionParam(Long amount, Long orderId) {
		SessionCreateParams.LineItem.PriceData.ProductData productData =
				SessionCreateParams.LineItem.PriceData.ProductData.builder()
						.setName("Salon Booking")
						.build();
		
		SessionCreateParams.LineItem.PriceData priceData =
				SessionCreateParams.LineItem.PriceData.builder()
						.setCurrency("usd")
						.setUnitAmount(amount)
						.setProductData(productData)
						.build();
		
		SessionCreateParams.LineItem lineItem =
				SessionCreateParams.LineItem.builder()
						.setQuantity(1L)
						.setPriceData(priceData)
						.build();
		
		return SessionCreateParams.builder()
				.setMode(SessionCreateParams.Mode.PAYMENT)
				.setSuccessUrl(buildSuccessUrl(orderId))
				.setCancelUrl(buildCancelUrl(orderId))
				.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
				.addLineItem(lineItem)
				.putMetadata("orderId", orderId.toString())
				.build();
	}
	
	private String buildSuccessUrl(Long orderId) {
		return "http://localhost:3000/payment-success/" + orderId;
	}
	
	private String buildCancelUrl(Long orderId) {
		return "http://localhost:3000/cancel/" + orderId;
	}
}


