package com.sparta.foodorder.global.common;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@MappedSuperclass
@Getter
public abstract class BaseDeleteEntity extends BaseCreateEntity {

	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	@Column(name = "deleted_by")
	private String deletedBy;

	/**
	 * Soft Delete 처리
	 */
	public void softDelete(String deletedBy) {
		this.deletedAt = LocalDateTime.now();
		this.deletedBy = deletedBy;
	}

	/**
	 * Soft Delete 여부 확인
	 */
	public boolean isDeleted() {
		return deletedAt != null;
	}
}