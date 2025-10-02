package com.sparta.foodorder.domain.address.domain;

import com.sparta.foodorder.domain.address.application.dto.AddressRequestDto;
import com.sparta.foodorder.domain.address.application.dto.AddressResponseDto;

import java.util.List;

public interface AddressService {

    /**
     * 주소 등록
     */
    AddressResponseDto createAddress(Long userId, AddressRequestDto request);

    /**
     * 사용자의 모든 주소 조회
     */
    List<AddressResponseDto> getUserAddresses(Long userId);

    /**
     * 주소 상세 조회
     */
    AddressResponseDto getAddress(Long addressId, Long userId);

    /**
     * 주소 수정
     */
    AddressResponseDto updateAddress(Long addressId, Long userId, AddressRequestDto request);

    /**
     * 주소 삭제
     */
    void deleteAddress(Long addressId, Long userId);

    /**
     * 기본 배송지 설정
     */
    AddressResponseDto setDefaultAddress(Long addressId, Long userId);

    /**
     * 기본 배송지 조회
     */
    AddressResponseDto getDefaultAddress(Long userId);
}

