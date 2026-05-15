package com.jo.marketplace.repository.interfaces;

import com.jo.marketplace.entity.MasUserShopRoleEntity;
import com.jo.marketplace.entity.MasUserShopRoleId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MasUserShopRoleRepository extends JpaRepository<MasUserShopRoleEntity, MasUserShopRoleId> {

    boolean existsByUserIdAndShopId(UUID userId, UUID shopId);

    @EntityGraph(attributePaths = {"user", "role"})
    List<MasUserShopRoleEntity> findByShopId(UUID shopId);

    @EntityGraph(attributePaths = {"shop", "role"})
    List<MasUserShopRoleEntity> findByUserId(UUID userId);

    @EntityGraph(attributePaths = {"user", "role"})
    Optional<MasUserShopRoleEntity> findFirstByShopIdAndUserId(UUID shopId, UUID userId);

    void deleteByShopIdAndUserId(UUID shopId, UUID userId);
}
