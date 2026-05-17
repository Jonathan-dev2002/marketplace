package com.jo.marketplace.model.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PageResponse<T> {
    private List<T> items;
    private long totalItems;
    private int totalPages;
    private int page;
    private int size;
}
