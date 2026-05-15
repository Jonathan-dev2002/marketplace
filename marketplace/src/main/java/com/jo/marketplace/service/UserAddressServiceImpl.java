package com.jo.marketplace.service;

import com.jo.marketplace.entity.UserAddressEntity;
import com.jo.marketplace.exception.BusinessException;
import com.jo.marketplace.model.dto.request.CreateUserAddressRequest;
import com.jo.marketplace.model.dto.request.UpdateUserAddressRequest;
import com.jo.marketplace.model.dto.response.UserAddressResponse;
import com.jo.marketplace.repository.interfaces.UserAddressRepository;
import com.jo.marketplace.service.interfaces.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.jo.marketplace.constant.StatusCodeEnums.ADDRESS_NOT_FOUND_404;

@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository addressRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserAddressResponse> getMyAddresses(UUID userId) {
        return addressRepository.findByUserIdAndDeletedAtIsNullOrderByDefaultAddressDescCreatedDateDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAddressResponse createAddress(UUID userId, CreateUserAddressRequest request) {
        boolean firstAddress = !addressRepository.existsByUserIdAndDeletedAtIsNull(userId);
        boolean shouldBeDefault = firstAddress || Boolean.TRUE.equals(request.getDefaultAddress());

        if (shouldBeDefault) {
            addressRepository.clearDefaultAddress(userId);
        }

        UserAddressEntity address = new UserAddressEntity();
        address.setUserId(userId);
        address.setLabel(normalizeNullable(request.getLabel()));
        address.setRecipientName(request.getRecipientName().trim());
        address.setPhone(request.getPhone().trim());
        address.setAddressLine1(request.getAddressLine1().trim());
        address.setAddressLine2(normalizeNullable(request.getAddressLine2()));
        address.setSubDistrict(normalizeNullable(request.getSubDistrict()));
        address.setDistrict(normalizeNullable(request.getDistrict()));
        address.setProvince(normalizeNullable(request.getProvince()));
        address.setPostalCode(request.getPostalCode().trim());
        address.setCountry(normalizeCountry(request.getCountry()));
        address.setDefaultAddress(shouldBeDefault);

        return toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional(readOnly = true)
    public UserAddressResponse getAddress(UUID userId, UUID addressId) {
        return toResponse(getAddressOrThrow(userId, addressId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAddressResponse updateAddress(UUID userId, UUID addressId, UpdateUserAddressRequest request) {
        UserAddressEntity address = getAddressOrThrow(userId, addressId);

        if (request.getLabel() != null) {
            address.setLabel(normalizeNullable(request.getLabel()));
        }
        if (StringUtils.hasText(request.getRecipientName())) {
            address.setRecipientName(request.getRecipientName().trim());
        }
        if (StringUtils.hasText(request.getPhone())) {
            address.setPhone(request.getPhone().trim());
        }
        if (StringUtils.hasText(request.getAddressLine1())) {
            address.setAddressLine1(request.getAddressLine1().trim());
        }
        if (request.getAddressLine2() != null) {
            address.setAddressLine2(normalizeNullable(request.getAddressLine2()));
        }
        if (request.getSubDistrict() != null) {
            address.setSubDistrict(normalizeNullable(request.getSubDistrict()));
        }
        if (request.getDistrict() != null) {
            address.setDistrict(normalizeNullable(request.getDistrict()));
        }
        if (request.getProvince() != null) {
            address.setProvince(normalizeNullable(request.getProvince()));
        }
        if (StringUtils.hasText(request.getPostalCode())) {
            address.setPostalCode(request.getPostalCode().trim());
        }
        if (request.getCountry() != null) {
            address.setCountry(normalizeCountry(request.getCountry()));
        }
        if (Boolean.TRUE.equals(request.getDefaultAddress())) {
            addressRepository.clearDefaultAddress(userId);
            address.setDefaultAddress(true);
        }

        return toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAddress(UUID userId, UUID addressId) {
        UserAddressEntity address = getAddressOrThrow(userId, addressId);
        boolean wasDefault = Boolean.TRUE.equals(address.getDefaultAddress());

        address.setDefaultAddress(false);
        address.setDeletedAt(LocalDateTime.now());
        addressRepository.save(address);

        if (wasDefault) {
            addressRepository.findFirstByUserIdAndDeletedAtIsNullOrderByCreatedDateDesc(userId)
                    .ifPresent(nextDefault -> {
                        nextDefault.setDefaultAddress(true);
                        addressRepository.save(nextDefault);
                    });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserAddressResponse setDefaultAddress(UUID userId, UUID addressId) {
        UserAddressEntity address = getAddressOrThrow(userId, addressId);
        addressRepository.clearDefaultAddress(userId);
        address.setDefaultAddress(true);
        return toResponse(addressRepository.save(address));
    }

    private UserAddressEntity getAddressOrThrow(UUID userId, UUID addressId) {
        return addressRepository.findByIdAndUserIdAndDeletedAtIsNull(addressId, userId)
                .orElseThrow(() -> new BusinessException(ADDRESS_NOT_FOUND_404, ADDRESS_NOT_FOUND_404.getDescriptionTH()));
    }

    private String normalizeNullable(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private String normalizeCountry(String country) {
        if (!StringUtils.hasText(country)) {
            return "Thailand";
        }
        return country.trim();
    }

    private UserAddressResponse toResponse(UserAddressEntity address) {
        return UserAddressResponse.builder()
                .id(address.getId())
                .label(address.getLabel())
                .recipientName(address.getRecipientName())
                .phone(address.getPhone())
                .addressLine1(address.getAddressLine1())
                .addressLine2(address.getAddressLine2())
                .subDistrict(address.getSubDistrict())
                .district(address.getDistrict())
                .province(address.getProvince())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .defaultAddress(address.getDefaultAddress())
                .build();
    }
}
