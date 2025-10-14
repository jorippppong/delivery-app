package com.sparta.foodorder.domain.user.application;

import com.sparta.foodorder.domain.user.application.dto.*;
import com.sparta.foodorder.domain.user.domain.User;
import com.sparta.foodorder.domain.user.domain.UserService;
import com.sparta.foodorder.domain.user.domain.UserStatus;
import com.sparta.foodorder.domain.user.infrastructure.UserRepository;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto signup(SignupRequestDto request) {
        if (userRepository.findByUserEmail(request.getUserEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        
        if (userRepository.findByNickName(request.getNickName()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = request.toEntity(encodedPassword);
        User savedUser = userRepository.save(user);

        return UserResponseDto.from(savedUser);
    }
    
    @Override
    @Transactional
    public UserResponseDto signupOwner(OwnerSignupRequestDto request) {
        if (userRepository.findByUserEmail(request.getUserEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        
        if (userRepository.findByNickName(request.getNickName()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }
        
        if (userRepository.findByBusinessNumber(request.getBusinessNumber()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_BUSINESS_NUMBER);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = request.toEntity(encodedPassword);
        User savedUser = userRepository.save(user);

        return UserResponseDto.from(savedUser);
    }
    
    @Override
    @Transactional
    public UserResponseDto signupManager(ManagerSignupRequestDto request) {
        if (userRepository.findByUserEmail(request.getUserEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        
        if (userRepository.findByNickName(request.getNickName()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = request.toEntity(encodedPassword);
        User savedUser = userRepository.save(user);

        return UserResponseDto.from(savedUser);
    }
    
    @Override
    @Transactional
    public UserResponseDto signupMaster(MasterSignupRequestDto request) {
        if (userRepository.findByUserEmail(request.getUserEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        
        if (userRepository.findByNickName(request.getNickName()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = request.toEntity(encodedPassword);
        User savedUser = userRepository.save(user);

        return UserResponseDto.from(savedUser);
    }

    @Override
    public UserResponseDto getUser(String userEmail) {
        User user = findUserByEmail(userEmail);
        validateUserAccess(user);
        return UserResponseDto.from(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateProfile(String userEmail, UpdateProfileRequestDto request) {
        User user = findUserByEmail(userEmail);
        validateUserAccess(user);

        userRepository.findByUserEmail(request.getUserEmail())
                .ifPresent(existingUser -> {
                    if (!existingUser.getUserEmail().equals(userEmail)) {
                        throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
                    }
                });

        user.updateProfile(request.getUserEmail(), request.getUserPhone());

        return UserResponseDto.from(user);
    }

    @Override
    @Transactional
    public void updatePassword(String userEmail, UpdatePasswordRequestDto request) {
        User user = findUserByEmail(userEmail);
        validateUserAccess(user);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
        
        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new BusinessException(ErrorCode.SAME_PASSWORD);
        }

        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.changePassword(encodedNewPassword);
    }

    @Override
    @Transactional
    public void deactivateUser(String userEmail) {
        User user = findUserByEmail(userEmail);
        validateUserAccess(user);
        
        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new BusinessException(ErrorCode.USER_WITHDRAWN);
        }
        
        user.deactivate();
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public User findUserByEmail(String userEmail) {
        return userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
    
    
    @Override
    public Page<UserResponseDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserResponseDto::from);
    }
    
    @Override
    @Transactional
    public UserResponseDto updateUserStatus(Long userId, UserStatusUpdateRequestDto request) {
        User user = findUserById(userId);
        
        UserStatus newStatus = request.getStatus();
        
        switch (newStatus) {
            case ACTIVE -> user.activate();
            case BANNED -> user.ban();
            case WITHDRAWN -> user.deactivate();
            default -> throw new BusinessException(ErrorCode.INVALID_USER_STATUS);
        }
        
        return UserResponseDto.from(user);
    }
    
    @Override
    @Transactional
    public void forceDeleteUser(Long userId) {
        User user = findUserById(userId);
        user.deactivate();  
    }
    
    @Override
    public Page<UserResponseDto> getBannedUsers(Pageable pageable) {
        return userRepository.findByStatus(UserStatus.BANNED, pageable)
                .map(UserResponseDto::from);
    }
    
    @Override
    public Page<UserResponseDto> getWithdrawnUsers(Pageable pageable) {
        return userRepository.findByStatus(UserStatus.WITHDRAWN, pageable)
                .map(UserResponseDto::from);
    }
    
    @Override
    public Page<UserResponseDto> getPendingUsers(Pageable pageable) {
        return userRepository.findByStatus(UserStatus.PENDING, pageable)
                .map(UserResponseDto::from);
    }
    
    @Override
    @Transactional
    public UserResponseDto approveUser(Long userId, ApprovalRequestDto request) {
        User user = findUserById(userId);
        
        if (user.getStatus() != UserStatus.PENDING) {
            throw new BusinessException(ErrorCode.USER_NOT_PENDING);
        }
        
        if (request.getApproved()) {
            user.approve();
        } else {
            user.reject();
        }
        
        return UserResponseDto.from(user);
    }
    
    private void validateUserAccess(User user) {
        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new BusinessException(ErrorCode.USER_WITHDRAWN);
        }
        
        if (user.getStatus() == UserStatus.BANNED) {
            throw new BusinessException(ErrorCode.USER_BANNED);
        }
        
        if (user.getStatus() == UserStatus.PENDING) {
            throw new BusinessException(ErrorCode.USER_PENDING_APPROVAL);
        }
        
        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new BusinessException(ErrorCode.USER_DEACTIVATED);
        }
    }
}
