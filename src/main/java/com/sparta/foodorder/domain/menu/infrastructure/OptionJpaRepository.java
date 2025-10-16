package com.sparta.foodorder.domain.menu.infrastructure;

import com.sparta.foodorder.domain.menu.domain.Option;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OptionJpaRepository extends JpaRepository<Option, UUID> {

    List<Option> findAllByMenuId(UUID menuId);

    List<Option> findAllByMenuIdAndDeletedAtIsNull(UUID menuId);

    Optional<Option> findByIdAndMenu_IdAndDeletedAtIsNull(UUID optionId, UUID menuId);

    boolean existsByIdAndMenu_idAndDeletedAtIsNull(UUID optionId, UUID menuId);
}
