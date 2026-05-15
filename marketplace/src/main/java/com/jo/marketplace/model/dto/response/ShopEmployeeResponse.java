package com.jo.marketplace.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ShopEmployeeResponse {

    private UUID userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private UUID roleId;
    private String roleName;
    private Boolean owner;
}
