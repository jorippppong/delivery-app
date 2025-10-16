package com.sparta.foodorder.domain.menu.infrastructure;

import com.sparta.foodorder.domain.menu.domain.Option;
import com.sparta.foodorder.domain.menu.domain.OptionValue;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OptionValueJpaRepository extends JpaRepository<OptionValue, UUID> {

    Optional<OptionValue> findByIdAndOption_IdAndDeletedAtIsNull(UUID optionValueId, UUID optionId);

    List<OptionValue> findAllByOptionId(UUID optionId);

    List<OptionValue> findAllByOptionIdAndDeletedAtIsNull(UUID optionId);

    List<OptionValue> option(Option option);
}
