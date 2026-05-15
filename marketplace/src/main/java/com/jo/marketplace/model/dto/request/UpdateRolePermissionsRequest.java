package com.jo.marketplace.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateRolePermissionsRequest {

    @NotNull(message = "กรุณาระบุ Permission")
    private List<String> permissionSlugs;
}
