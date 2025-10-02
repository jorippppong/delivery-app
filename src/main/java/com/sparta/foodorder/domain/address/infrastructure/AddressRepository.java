package com.sparta.foodorder.domain.address.infrastructure;

import com.sparta.foodorder.domain.address.domain.Address;
import com.sparta.foodorder.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    List<Address> findByUserOrderByCreatedAtDesc(User user);
    
    Optional<Address> findByUserAndIsDefaultTrue(User user);
        
    void deleteByUser(User user);
    
    Optional<Address> findByIdAndUserId(Long addressId, Long userId);
    
    List<Address> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);
}

