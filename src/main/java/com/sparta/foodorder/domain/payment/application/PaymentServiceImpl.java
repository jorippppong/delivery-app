package com.sparta.foodorder.domain.payment.application;

import org.springframework.stereotype.Service;

import com.sparta.foodorder.domain.payment.domain.PaymentRepository;

@Service
public class PaymentServiceImpl {

	private final PaymentRepository paymentRepository;

	public PaymentServiceImpl(PaymentRepository paymentRepository) {
		this.paymentRepository = paymentRepository;
	}
}
