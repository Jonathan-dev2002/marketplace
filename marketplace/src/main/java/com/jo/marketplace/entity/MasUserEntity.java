package com.jo.marketplace.entity;

import com.jo.marketplace.model.enums.UserStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
@SQLRestriction("deleted_at IS NULL")
public class MasUserEntity extends BaseEntity {

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(length = 20)
    private String phone;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    // ⚡ เปลี่ยนจาก String เป็น Enum พร้อมสั่งให้ Hibernate เซฟลง DB เป็นตัวอักษร
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private UserStatusEnum status = UserStatusEnum.ACTIVE;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MasUserShopRoleEntity> shopRoles = new ArrayList<>();
}