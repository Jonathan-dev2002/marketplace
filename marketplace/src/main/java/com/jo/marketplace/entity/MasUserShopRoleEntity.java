package com.jo.marketplace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Table(name = "user_shop_roles")
@IdClass(MasUserShopRoleId.class)
@Getter
@Setter
public class MasUserShopRoleEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Id
    @Column(name = "shop_id") // ปล่อย NULL ได้ถ้าเป็นสิทธิ์ระดับ Platform
    private UUID shopId;

    @Id
    @Column(name = "role_id")
    private UUID roleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private MasUserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private MasRoleEntity role;
}