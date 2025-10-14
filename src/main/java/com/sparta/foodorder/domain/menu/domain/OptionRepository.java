package com.sparta.foodorder.domain.menu.domain;

import java.util.UUID;

public interface OptionRepository {
    Option findByMenuId(UUID menuId);
    Option save(Option option);
}
