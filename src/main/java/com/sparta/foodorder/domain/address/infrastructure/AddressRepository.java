package com.sparta.foodorder.domain.address.infrastructure;

import com.sparta.foodorder.domain.address.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
