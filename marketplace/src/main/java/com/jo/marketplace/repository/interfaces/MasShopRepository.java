package com.jo.marketplace.repository.interfaces;

import com.jo.marketplace.entity.MasShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MasShopRepository extends JpaRepository<MasShopEntity, UUID> {

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    boolean existsByNameAndIdNot(String name, UUID id);

    boolean existsBySlugAndIdNot(String slug, UUID id);

    List<MasShopEntity> findByOwnerId(UUID ownerId);
}
