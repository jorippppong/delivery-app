package com.sparta.foodorder.domain.menu.domain;

import java.util.List;
import java.util.UUID;

public interface OptionValueRepository {
    OptionValue save(OptionValue optionValue);
    List<OptionValue> findAllByOptionId(UUID optionId);
    List<OptionValue> findAllByOptionIdAndDeletedAtIsNull(UUID optionId);

}
