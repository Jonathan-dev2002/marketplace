package com.jo.marketplace.repository.projection;

import com.jo.marketplace.model.enums.UserStatusEnum;

import java.util.UUID;

public interface UserProfileProjection {
    UUID getId();
    String getUsername();
    String getEmail();
    String getFirstName();
    String getLastName();
    String getPhone();
    UserStatusEnum getStatus();
}
