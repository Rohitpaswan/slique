package com.example.paymentservice.service;


import com.example.paymentservice.domain.GatewayType;
import com.example.paymentservice.domain.PaymentMethod;
import com.example.paymentservice.domain.PaymentOrderStatus;
import com.example.paymentservice.exception.payment.MissingIdempotencyKeyException;
import com.example.paymentservice.exception.payment.PaymentGatewayException;
import com.example.paymentservice.exception.payment.PaymentOrderBrokenException;
import com.example.paymentservice.exception.payment.PaymentOrderNotFoundException;
import com.example.paymentservice.model.PaymentOrder;
import com.example.paymentservice.payload.dto.BookingDto;
import com.example.paymentservice.model.PaymentRequest;
import com.example.paymentservice.payload.dto.UserDto;
import com.example.paymentservice.payload.response.PaymentLinkResponse;
import com.example.paymentservice.gateway.AbstractPaymentGateway;
import com.example.paymentservice.repository.PaymentOrderRepository;
import com.example.paymentservice.strategy.PaymentGatewayFactory;
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

        if (idempotencyKey == null || idempotencyKey.isBlank()) { throw new MissingIdempotencyKeyException("Missing idempotencyKey");}

            Optional<PaymentOrder> existing = paymentOrderRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                PaymentOrder existingOrder = existing.get();
                if(PaymentOrderStatus.INITIATED.equals(existingOrder.getStatus()) ||
                     PaymentOrderStatus.SUCCESS.equals(existingOrder.getStatus())) {
                    log.info("Duplicate request detected (idempotency key: {}). " +
                            "Returning existing order: {}", idempotencyKey, existing.get().getId());
                    return buildResponseFromExisting(existing.get());
                }
                boolean isBrokenOrder = (existingOrder.getPaymentLink() == null || existingOrder.getPaymentLinkId() == null)
                        && (PaymentOrderStatus.PENDING.equals(existingOrder.getStatus()) || PaymentOrderStatus.FAILED.equals(existingOrder.getStatus()));


                if(isBrokenOrder) {
                    log.warn("Broken order-Id {}, status{} ", existingOrder.getId(), existingOrder.getStatus());
                    existingOrder.setStatus(PaymentOrderStatus.FAILED);
                    paymentOrderRepository.save(existingOrder);
                  throw  new PaymentOrderBrokenException("Payment order is in a broken state.Order Id: ", existingOrder.getId());

                }


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
        paymentOrder.setUserId(userDto.getId());
        paymentOrder.setSalonId(bookingDto.getSalonId());
        paymentOrder.setPaymentMethod(paymentMethod);
        paymentOrder.setAmount(amount);
        paymentOrder.setIdempotencyKey(idempotencyKey);
        paymentOrder.setStatus(PaymentOrderStatus.PENDING);
        paymentOrderRepository.save(paymentOrder);
        AbstractPaymentGateway selectedGateway = gateway.createPaymentGateway(
                GatewayType.fromPaymentMethod(paymentMethod));

        try {

            log.info("selected Gateway{}", selectedGateway);
            PaymentRequest paymentRequest = getPaymentRequest(userDto, bookingDto, paymentOrder);
            PaymentLinkResponse paymentLink = selectedGateway.createPaymentFlow(paymentRequest);

            paymentOrder.setPaymentLink(paymentLink.getPayment_link_url());
            paymentOrder.setPaymentLinkId(paymentLink.getGetPayment_link_id());
            paymentOrder.setStatus(PaymentOrderStatus.INITIATED);
            log.warn("paymentOrder{}", paymentOrder);
            paymentOrderRepository.save(paymentOrder);

            return paymentLink;
        } catch (Exception e) {
            log.error("Gateway failed for order {}", paymentOrder.getId(), e);
            paymentOrder.setStatus(PaymentOrderStatus.FAILED);
            paymentOrderRepository.save(paymentOrder);
            throw new PaymentGatewayException(selectedGateway.getClass().getSimpleName() + "Failed to create payment link for order" + paymentOrder.getId(), e);
        }
    }

    private PaymentLinkResponse buildResponseFromExisting(PaymentOrder existingOrder) {
        PaymentLinkResponse response = new PaymentLinkResponse();

        response.setPayment_link_url(existingOrder.getPaymentLink());
        response.setGetPayment_link_id(existingOrder.getPaymentLinkId());

        return response;
    }


    @Override
    public PaymentOrder getPaymentOrderById(Long id) {
        return paymentOrderRepository.findById(id).orElseThrow(() -> new PaymentOrderNotFoundException( "Payment order not found for id: ", id));
    }

    @Override
    public PaymentOrder getPaymentOrderByPaymentId(String paymentId) {
        return paymentOrderRepository.findByPaymentLinkId(paymentId);
    }




}


