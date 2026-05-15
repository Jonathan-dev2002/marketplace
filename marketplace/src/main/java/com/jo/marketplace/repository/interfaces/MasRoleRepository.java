package com.jo.marketplace.repository.interfaces;

import com.jo.marketplace.entity.MasRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MasRoleRepository extends JpaRepository<MasRoleEntity, UUID> {

    Optional<MasRoleEntity> findByNameAndIsSystemDefinedTrue(String name);

    Optional<MasRoleEntity> findByNameAndShopId(String name, UUID shopId);

    boolean existsByNameAndShopId(String name, UUID shopId);

    @EntityGraph(attributePaths = "permissions")
    List<MasRoleEntity> findByShopIdOrIsSystemDefinedTrue(UUID shopId);

    @EntityGraph(attributePaths = "permissions")
    @Query("select r from MasRoleEntity r where r.id = :id")
    Optional<MasRoleEntity> findWithPermissionsById(@Param("id") UUID id);
}
