package com.example.paymentservice.service;


import com.example.paymentservice.domain.GatewayType;
import com.example.paymentservice.domain.PaymentMethod;
import com.example.paymentservice.domain.PaymentOrderStatus;
import com.example.paymentservice.model.PaymentOrder;
import com.example.paymentservice.payload.dto.BookingDto;
import com.example.paymentservice.model.PaymentRequest;
import com.example.paymentservice.payload.dto.UserDto;
import com.example.paymentservice.payload.response.PaymentLinkResponse;
import com.example.paymentservice.gateway.AbstractPaymentGateway;
import com.example.paymentservice.repository.PaymentOrderRepository;
import com.example.paymentservice.strategy.PaymentGatewayFactory;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentOrderRepository paymentOrderRepository;
    private final RazorpayClient razorpayClient;
    private final PaymentGatewayFactory gateway;

    @NotNull
    private static PaymentRequest getPaymentRequest(UserDto userDto, BookingDto bookingDto, PaymentOrder paymentOrder) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(paymentOrder.getId());
        paymentRequest.setAmount(paymentOrder.getAmount());
        paymentRequest.setCurrency("INR");
        paymentRequest.setCustomerName(userDto.getFirstName() + " " + userDto.getLastName());
        paymentRequest.setCustomerEmail(userDto.getEmail());
        paymentRequest.setBookingId(bookingDto.getId());
        paymentRequest.setUserId(paymentOrder.getUserId());
        paymentRequest.setSalonId(paymentOrder.getSalonId());
        return paymentRequest;
    }

    @Override
    public PaymentLinkResponse createOrder(UserDto userDto, BookingDto bookingDto, PaymentMethod paymentMethod, String idempotencyKey) {

        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            Optional<PaymentOrder> existing = paymentOrderRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                log.info("Duplicate request detected (idempotency key: {}). " +
                        "Returning existing order: {}", idempotencyKey, existing.get().getId());
                return buildResponseFromExisting(existing.get());
            }
            //need to fix , doubt go with pending+ initaied or only initied
			/*
			we are sending payment link , right if active payment decided based on id and pending or initiaed, . problem is if payment initiated, ok u can send link. but in pending status why send?? must be initiated right??
			 */
            Optional<PaymentOrder> activePayment = paymentOrderRepository
                    .findByBookingIdAndStatusIn(bookingDto.getId(), List.of(PaymentOrderStatus.INITIATED));
            log.info("activePayment {}", activePayment);
            if (activePayment.isPresent()) {
                return buildResponseFromExisting(activePayment.get()); // same helper
            }

        }


        BigDecimal totalPrice = bookingDto.getTotalPrice();
        Long amount = totalPrice
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        PaymentOrder paymentOrder = new PaymentOrder();

        paymentOrder.setBookingId(bookingDto.getId());
        paymentOrder.setSalonId(bookingDto.getSalonId());
        paymentOrder.setUserId(userDto.getId());
        paymentOrder.setSalonId(bookingDto.getSalonId());
        paymentOrder.setPaymentMethod(paymentMethod);
        paymentOrder.setAmount(amount);
        paymentOrder.setIdempotencyKey(idempotencyKey);

        paymentOrder.setStatus(PaymentOrderStatus.PENDING);
        paymentOrderRepository.save(paymentOrder);

        AbstractPaymentGateway selectedGatway = gateway.createPaymentGateway(
                GatewayType.fromPaymentMethod(paymentMethod));

        log.info("selected Gateway{}", selectedGatway);
        PaymentRequest paymentRequest = getPaymentRequest(userDto, bookingDto, paymentOrder);
        PaymentLinkResponse paymentLink = selectedGatway.createPaymentFlow(paymentRequest);

        paymentOrder.setPaymentLink(paymentLink.getPayment_link_url());
        paymentOrder.setPaymentLinkId(paymentLink.getGetPayment_link_id());
        paymentOrder.setStatus(PaymentOrderStatus.INITIATED);
        log.warn("paymentOrder{}", paymentOrder);
        paymentOrderRepository.save(paymentOrder);

        return paymentLink;

    }

    private PaymentLinkResponse buildResponseFromExisting(PaymentOrder existingOrder) {
        PaymentLinkResponse response = new PaymentLinkResponse();

        response.setPayment_link_url(existingOrder.getPaymentLink());
        response.setGetPayment_link_id(existingOrder.getPaymentLinkId());

        return response;
    }


    @Override
    public PaymentOrder getPaymentOrderById(Long id) {
        return paymentOrderRepository.findById(id).orElseThrow(() -> new RuntimeException("Payment order not found" + id));
    }

    @Override
    public PaymentOrder getPaymentOrderByPaymentId(String paymentId) {
        return paymentOrderRepository.findByPaymentLinkId(paymentId);
    }



    public String createStripePaymentLink(UserDto userDto, Long amount, Long orderId) {
        try {
            Session session = Session.create(buildSessionParam(amount, orderId));
            return session.getUrl();

        } catch (Exception e) {
            throw new RuntimeException("Error creating Stripe session", e);
        }
    }

    @Override
    public boolean confirmPayment(PaymentOrder paymentOrder, String paymentId) {

        if (PaymentOrderStatus.PENDING.equals(paymentOrder.getStatus())) {
            if (PaymentMethod.RAZORPAY.equals(paymentOrder.getPaymentMethod())) {
                try {
                    Payment payment = razorpayClient.payments.fetch(paymentId);
                    String status = payment.get("status");
                    if (status.equals("captured")) {
                        paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
                        paymentOrderRepository.save(paymentOrder);
                        return true;
                    }
                } catch (RazorpayException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
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


