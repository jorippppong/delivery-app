package com.sparta.foodorder.domain.store.infrastructure;

import com.sparta.foodorder.domain.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreJpaRepository extends JpaRepository<Store, Long> {

}
