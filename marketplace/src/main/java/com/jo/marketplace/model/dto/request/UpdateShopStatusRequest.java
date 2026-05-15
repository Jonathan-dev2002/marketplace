package com.jo.marketplace.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateShopStatusRequest {

    @NotNull(message = "กรุณาระบุสถานะร้านค้า")
    private Boolean active;
}
