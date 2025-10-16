package com.sparta.foodorder.domain.menu.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface OptionRepository {

    Option findByMenuId(UUID menuId);

    List<Option> findAllByMenuId(UUID menuId);

    Option save(Option option);

    Optional<Option> findById(UUID id);

    Optional<Option> findByIdAndMenuIdAndDeletedAtIsNull(UUID optionId, UUID menuId);

    boolean existsByIdAndMenuIdAndDeletedAtIsNull(UUID optionId, UUID menuId);

  
    List<Option> findAllByMenuIdAndDeletedAtIsNull(UUID menuId);

}
