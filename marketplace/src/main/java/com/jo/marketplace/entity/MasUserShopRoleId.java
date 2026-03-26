package com.jo.marketplace.entity;
import lombok.Data;
import java.io.Serializable;
import java.util.UUID;

@Data
public class MasUserShopRoleId implements Serializable {
    private UUID userId;
    private UUID shopId;
    private UUID roleId;
}