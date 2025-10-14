package com.sparta.foodorder.domain.ai.infrastructure;

import com.sparta.foodorder.domain.ai.domain.AiLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AiLogRepository extends JpaRepository<AiLog, UUID> {
}
