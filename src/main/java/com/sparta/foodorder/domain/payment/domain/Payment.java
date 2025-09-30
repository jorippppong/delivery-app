package com.sparta.foodorder.domain.payment.domain;

import java.time.LocalDateTime;
import java.util.UUID;

import com.sparta.foodorder.global.common.BaseUpdateEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseUpdateEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "payment_public_id", nullable = false, unique = true, updatable = false, columnDefinition = "BINARY(16)")
	private UUID paymentPublicId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "order_id", nullable = false, unique = true)
	private Long orderId;

	@Column(nullable = false)
	private Integer amount;

	@Column(name = "payment_method", nullable = false)
	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;

	@Column(name = "pg_transaction_id", nullable = false)
	private String pgTransactionId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private PaymentStatus status;

	@Column(name = "fail_reason")
	private String failReason;

	@Column(name = "paid_at")
	private LocalDateTime paidAt;

	@Column(name = "failed_at")
	private LocalDateTime failedAt;

	@Column(name = "refunded_at")
	private LocalDateTime refundedAt;

	private Payment(Long userId, Long orderId, Integer amount,
		PaymentMethod paymentMethod, String pgTransactionId) {
		this.userId = userId;
		this.orderId = orderId;
		this.amount = amount;
		this.paymentMethod = paymentMethod;
		this.pgTransactionId = pgTransactionId;
		this.status = PaymentStatus.READY;
	}

	public static Payment createPayment(Long userId, Long orderId, Integer amount,
		PaymentMethod paymentMethod, String pgTransactionId) {
		return new Payment(userId, orderId, amount, paymentMethod, pgTransactionId);
	}

	@PrePersist
	public void generatePaymentPublicId() {
		if (this.paymentPublicId == null) {
			this.paymentPublicId = UUID.randomUUID();
		}
	}

	public void completePayment() {
		this.status = PaymentStatus.PAID;
		this.paidAt = LocalDateTime.now();
	}

	public void failPayment(String reason) {
		this.status = PaymentStatus.FAILED;
		this.failReason = reason;
		this.failedAt = LocalDateTime.now();
	}

	public void cancelPayment(String reason) {
		this.status = PaymentStatus.CANCELLED;
		this.failReason = reason;
		this.failedAt = LocalDateTime.now();
	}

	public void refundPayment(String reason) {
		this.status = PaymentStatus.REFUNDED;
		this.failReason = reason;
		this.refundedAt = LocalDateTime.now();
	}

	public boolean isRefundableOrCancellable() {
		return this.status == PaymentStatus.PAID;
	}
}