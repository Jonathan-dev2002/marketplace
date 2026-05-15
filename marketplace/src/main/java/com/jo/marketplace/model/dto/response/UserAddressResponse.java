package com.jo.marketplace.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserAddressResponse {
    private UUID id;
    private String label;
    private String recipientName;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String subDistrict;
    private String district;
    private String province;
    private String postalCode;
    private String country;
    private Boolean defaultAddress;
}
