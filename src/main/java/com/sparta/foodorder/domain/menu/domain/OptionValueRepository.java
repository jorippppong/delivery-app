package com.sparta.foodorder.domain.menu.domain;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface OptionValueRepository {

    Optional<OptionValue> findByIdAndOptionIdAndDeletedAtIsNull(UUID optionValueId, UUID optionId);

    OptionValue save(OptionValue optionValue);
  
    List<OptionValue> findAllByOptionId(UUID optionId);
  
    List<OptionValue> findAllByOptionIdAndDeletedAtIsNull(UUID optionId);

}
