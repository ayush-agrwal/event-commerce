package com.eventcommerce.payments.service;

import com.eventcommerce.contracts.events.PaymentSucceededEvent;
import com.eventcommerce.payments.model.Payment;
import com.eventcommerce.payments.producer.PaymentEventProducer;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PaymentService {

    private final List<Payment> payments = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final PaymentEventProducer paymentEventProducer;

    public PaymentService(PaymentEventProducer paymentEventProducer) {
        this.paymentEventProducer = paymentEventProducer;
    }

    public Payment createPayment(Payment payment) {

        payment.setId(idGenerator.getAndIncrement());
        payment.setStatus("SUCCESS");

        payments.add(payment);

        PaymentSucceededEvent event = new PaymentSucceededEvent(
                payment.getId(),
                payment.getOrderId(),
                BigDecimal.valueOf(payment.getAmount()),
                payment.getProductId(),
                payment.getQuantity());

        paymentEventProducer.publishPaymentSucceeded(event);

        return payment;
    }

    public Payment findByOrderId(Long orderId) {

        for (Payment payment : payments) {

            if (payment.getOrderId().equals(orderId)) {
                return payment;
            }
        }

        return null;
    }

    public Payment updatePayment(Payment payment) {

        for (int i = 0; i < payments.size(); i++) {

            if (payments.get(i).getId().equals(payment.getId())) {

                payments.set(i, payment);

                return payment;
            }
        }

        return null;
    }
    public List<Payment> getAllPayments() {
        return payments;
    }
}
