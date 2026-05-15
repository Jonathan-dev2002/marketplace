package com.jo.marketplace.model.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class ShopResponse {

    private UUID id;
    private UUID ownerId;
    private String name;
    private String slug;
    private String description;
    private String logoUrl;
    private Boolean verified;
    private Boolean active;
    private BigDecimal ratingAvg;
}
