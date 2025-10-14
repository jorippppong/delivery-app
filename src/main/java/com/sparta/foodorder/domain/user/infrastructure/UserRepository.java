package com.sparta.foodorder.domain.user.infrastructure;

import com.sparta.foodorder.domain.user.domain.User;
import com.sparta.foodorder.domain.user.domain.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserEmail(String userEmail);

    Optional<User> findByNickName(String nickName);
    
    Optional<User> findByBusinessNumber(String businessNumber);
    
    Page<User> findByStatus(UserStatus status, Pageable pageable);
}
