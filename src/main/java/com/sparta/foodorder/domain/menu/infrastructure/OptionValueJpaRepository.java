package com.sparta.foodorder.domain.menu.infrastructure;

import com.sparta.foodorder.domain.menu.domain.OptionValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OptionValueJpaRepository extends JpaRepository<OptionValue, UUID> {
    List<OptionValue> findAllByOptionId(UUID optionId);

}
