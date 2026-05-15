package com.jo.marketplace.repository.interfaces;

import com.jo.marketplace.entity.MasPermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MasPermissionRepository extends JpaRepository<MasPermissionEntity, UUID> {

    Optional<MasPermissionEntity> findBySlug(String slug);

    List<MasPermissionEntity> findBySlugIn(Collection<String> slugs);
}
