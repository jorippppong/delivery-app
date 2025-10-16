package com.sparta.foodorder.domain.menu.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<Option, UUID> {

    Option findByMenuId(UUID menuId);

    Option save(Option option);

    Optional<Option> findById(UUID id);

    Optional<Option> findByIdAndMenu_IdAndDeletedAtIsNull(UUID optionId, UUID menuId);

    boolean existsByIdAndMenu_idAndDeletedAtIsNull(UUID optionId, UUID menuId);
}
