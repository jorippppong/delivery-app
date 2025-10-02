package com.sparta.foodorder.domain.address.application;

import com.sparta.foodorder.domain.address.application.dto.AddressRequestDto;
import com.sparta.foodorder.domain.address.application.dto.AddressResponseDto;
import com.sparta.foodorder.domain.address.domain.Address;
import com.sparta.foodorder.domain.address.domain.AddressService;
import com.sparta.foodorder.domain.address.infrastructure.AddressRepository;
import com.sparta.foodorder.domain.user.domain.User;
import com.sparta.foodorder.domain.user.infrastructure.UserRepository;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public AddressResponseDto createAddress(Long userId, AddressRequestDto request) {
        User user = findUserById(userId);

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(Address::setAsNotDefault);
        }

        Address address = request.toEntity(user);
        Address savedAddress = addressRepository.save(address);

        return AddressResponseDto.from(savedAddress);
    }

    @Override
    public List<AddressResponseDto> getUserAddresses(Long userId) {
        List<Address> addresses = addressRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return addresses.stream()
                .map(AddressResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponseDto getAddress(Long addressId, Long userId) {
        Address address = findAddressByIdAndUserId(addressId, userId);
        return AddressResponseDto.from(address);
    }

    @Override
    @Transactional
    public AddressResponseDto updateAddress(Long addressId, Long userId, AddressRequestDto request) {
        Address address = findAddressByIdAndUserId(addressId, userId);

        address.updateAddress(
                request.getAddressLine(),
                request.getDetailAddress(),
                request.getPostalCode()
        );

        if (request.getRecipientName() != null || request.getRecipientPhone() != null) {
            address.updateRecipient(
                    request.getRecipientName(),
                    request.getRecipientPhone()
            );
        }

        if (Boolean.TRUE.equals(request.getIsDefault()) && !address.getIsDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(Address::setAsNotDefault);
            address.setAsDefault();
        }

        return AddressResponseDto.from(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        Address address = findAddressByIdAndUserId(addressId, userId);
        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public AddressResponseDto setDefaultAddress(Long addressId, Long userId) {
        Address address = findAddressByIdAndUserId(addressId, userId);

        addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .ifPresent(Address::setAsNotDefault);

        address.setAsDefault();

        return AddressResponseDto.from(address);
    }

    @Override
    public AddressResponseDto getDefaultAddress(Long userId) {
        Address address = addressRepository.findByUserIdAndIsDefaultTrue(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));

        return AddressResponseDto.from(address);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Address findAddressByIdAndUserId(Long addressId, Long userId) {
        return addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADDRESS_NOT_FOUND));
    }
}

