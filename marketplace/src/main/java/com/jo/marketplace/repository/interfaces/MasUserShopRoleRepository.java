package com.jo.marketplace.repository.interfaces;

import com.jo.marketplace.entity.MasUserShopRoleEntity;
import com.jo.marketplace.entity.MasUserShopRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasUserShopRoleRepository extends JpaRepository<MasUserShopRoleEntity, MasUserShopRoleId> {
}