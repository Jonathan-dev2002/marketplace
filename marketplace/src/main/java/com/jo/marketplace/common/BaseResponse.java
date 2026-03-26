package com.jo.marketplace.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
public class BaseResponse<T> {

    private Status status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    @Accessors(chain = true)
    @Data
    public static class Status {
        private String code;
        private String description;
    }
}