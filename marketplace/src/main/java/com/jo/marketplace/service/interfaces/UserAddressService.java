package com.jo.marketplace.service.interfaces;

import com.jo.marketplace.model.dto.request.CreateUserAddressRequest;
import com.jo.marketplace.model.dto.request.UpdateUserAddressRequest;
import com.jo.marketplace.model.dto.response.UserAddressResponse;

import java.util.List;
import java.util.UUID;

public interface UserAddressService {

    List<UserAddressResponse> getMyAddresses(UUID userId);

    UserAddressResponse createAddress(UUID userId, CreateUserAddressRequest request);

    UserAddressResponse getAddress(UUID userId, UUID addressId);

    UserAddressResponse updateAddress(UUID userId, UUID addressId, UpdateUserAddressRequest request);

    void deleteAddress(UUID userId, UUID addressId);

    UserAddressResponse setDefaultAddress(UUID userId, UUID addressId);
}
