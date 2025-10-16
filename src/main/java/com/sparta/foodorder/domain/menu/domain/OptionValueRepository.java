package com.sparta.foodorder.domain.menu.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionValueRepository extends JpaRepository<OptionValue, UUID> {

    Optional<OptionValue> findByIdAndOption_IdAndDeletedAtIsNull(UUID optionValueId, UUID optionId);
}
