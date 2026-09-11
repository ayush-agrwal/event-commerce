package com.eventcommerce.payments.service;

import com.eventcommerce.payments.model.Payment;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PaymentService {
    private final List<Payment> payments = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Payment createPayment(Payment payment) {
        payment.setId(idGenerator.getAndIncrement());
        payment.setStatus("SUCCESS");
        payments.add(payment);
        return payment;
    }

    public List<Payment> getAllPayments() {
        return payments;
    }
}