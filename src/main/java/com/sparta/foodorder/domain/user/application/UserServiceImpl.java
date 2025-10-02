package com.sparta.foodorder.domain.user.application;

import com.sparta.foodorder.domain.user.application.dto.SignupRequestDto;
import com.sparta.foodorder.domain.user.application.dto.UpdatePasswordRequestDto;
import com.sparta.foodorder.domain.user.application.dto.UpdateProfileRequestDto;
import com.sparta.foodorder.domain.user.application.dto.UserResponseDto;
import com.sparta.foodorder.domain.user.domain.User;
import com.sparta.foodorder.domain.user.domain.UserService;
import com.sparta.foodorder.domain.user.infrastructure.UserRepository;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
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
        return UserResponseDto.from(user);
    }

    @Override
    @Transactional
    public UserResponseDto updateProfile(String userEmail, UpdateProfileRequestDto request) {
        User user = findUserByEmail(userEmail);

        // 이메일 중복 체크 (본인 제외)
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

        // 현재 비밀번호 검증
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        // 새 비밀번호 암호화
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.changePassword(encodedNewPassword);
    }

    @Override
    @Transactional
    public void deactivateUser(String userEmail) {
        User user = findUserByEmail(userEmail);
        user.deactivate();
    }

    private User findUserByEmail(String userEmail) {
        return userRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
