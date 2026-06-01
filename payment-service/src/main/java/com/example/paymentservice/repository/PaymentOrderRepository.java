package com.example.paymentservice.repository;

import com.example.paymentservice.domain.PaymentOrderStatus;
import com.example.paymentservice.model.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder, Long> {
    PaymentOrder findByPaymentLinkId(String paymentLinkId);
    Optional<PaymentOrder> findByIdempotencyKey(String idempotencyKey);
    Optional<PaymentOrder> findByBookingIdAndStatusIn(Long bookingId, List<PaymentOrderStatus> statuses);

}
