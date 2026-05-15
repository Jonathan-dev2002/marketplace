package com.jo.marketplace.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChangeShopEmployeeRoleRequest {

    @NotNull(message = "กรุณาระบุ Role")
    private UUID roleId;
}
