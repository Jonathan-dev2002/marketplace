package com.jo.marketplace.repository.interfaces;

import com.jo.marketplace.entity.MasUserEntity;
import com.jo.marketplace.model.enums.UserStatusEnum;
import com.jo.marketplace.repository.projection.UserProfileProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MasUserRepository extends JpaRepository<MasUserEntity, UUID> {

    Optional<MasUserEntity> findByUsername(String username);
    Optional<MasUserEntity> findByEmail(String email);
    Optional<MasUserEntity> findByUsernameOrEmail(String username, String email);
    Optional<UserProfileProjection> findProfileById(UUID id);
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByIdAndStatus(UUID id, UserStatusEnum status);
}
