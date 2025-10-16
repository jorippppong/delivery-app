package com.sparta.foodorder.domain.menu.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OptionRepository {
    Optional<Option> findById(UUID optionId);
    Option findByMenuId(UUID menuId);
    List<Option> findAllByMenuId(UUID menuId);
    Option save(Option option);
    List<Option> findAllByMenuIdAndDeletedAtIsNull(UUID menuId);

}
