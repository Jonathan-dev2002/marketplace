package com.jo.marketplace.repository.interfaces;

import com.jo.marketplace.entity.UserAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserAddressRepository extends JpaRepository<UserAddressEntity, UUID> {

    List<UserAddressEntity> findByUserIdAndDeletedAtIsNullOrderByDefaultAddressDescCreatedDateDesc(UUID userId);

    Optional<UserAddressEntity> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);

    boolean existsByUserIdAndDeletedAtIsNull(UUID userId);

    Optional<UserAddressEntity> findFirstByUserIdAndDeletedAtIsNullOrderByCreatedDateDesc(UUID userId);

    @Modifying
    @Query("""
            update UserAddressEntity address
            set address.defaultAddress = false
            where address.userId = :userId
              and address.deletedAt is null
            """)
    void clearDefaultAddress(@Param("userId") UUID userId);
}
