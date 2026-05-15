package com.jo.marketplace.repository.interfaces;

import com.jo.marketplace.entity.MasRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MasRoleRepository extends JpaRepository<MasRoleEntity, UUID> {

    Optional<MasRoleEntity> findByNameAndIsSystemDefinedTrue(String name);
}
