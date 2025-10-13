package com.sparta.foodorder.domain.user.application;

import com.sparta.foodorder.domain.user.application.dto.SignupRequestDto;
import com.sparta.foodorder.domain.user.application.dto.UpdatePasswordRequestDto;
import com.sparta.foodorder.domain.user.application.dto.UpdateProfileRequestDto;
import com.sparta.foodorder.domain.user.application.dto.UserResponseDto;
import com.sparta.foodorder.domain.user.application.dto.UserStatusUpdateRequestDto;
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
        // 이메일 중복 체크
        if (userRepository.findByUserEmail(request.getUserEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        
        // 닉네임 중복 체크
        if (userRepository.findByNickName(request.getNickName()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 사용자 생성
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

        // 이메일 중복 체크 
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

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
        
        // 현재 비밀번호와 새 비밀번호 동일 여부 체크
        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new BusinessException(ErrorCode.SAME_PASSWORD);
        }

        // 새 비밀번호 암호화
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.changePassword(encodedNewPassword);
    }

    @Override
    @Transactional
    public void deactivateUser(String userEmail) {
        User user = findUserByEmail(userEmail);
        validateUserAccess(user);
        
        // 이미 탈퇴한 사용자인지 확인
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
    
    private void validateUserAccess(User user) {
        if (user.getStatus() == UserStatus.WITHDRAWN) {
            throw new BusinessException(ErrorCode.USER_WITHDRAWN);
        }
        
        if (user.getStatus() == UserStatus.BANNED) {
            throw new BusinessException(ErrorCode.USER_BANNED);
        }
        
        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new BusinessException(ErrorCode.USER_DEACTIVATED);
        }
    }
}
