package com.jo.marketplace.constant;

import java.util.List;

public final class PermissionConstants {

    private PermissionConstants() {
    }

    public static final String SHOP_VIEW = "SHOP_VIEW";
    public static final String SHOP_UPDATE = "SHOP_UPDATE";
    public static final String SHOP_STATUS_UPDATE = "SHOP_STATUS_UPDATE";
    public static final String SHOP_SLUG_UPDATE = "SHOP_SLUG_UPDATE";
    public static final String SHOP_DELETE = "SHOP_DELETE";
    public static final String SHOP_EMPLOYEE_VIEW = "SHOP_EMPLOYEE_VIEW";
    public static final String SHOP_EMPLOYEE_MANAGE = "SHOP_EMPLOYEE_MANAGE";
    public static final String SHOP_ROLE_VIEW = "SHOP_ROLE_VIEW";
    public static final String SHOP_ROLE_MANAGE = "SHOP_ROLE_MANAGE";
    public static final String SHOP_CHAT_VIEW = "SHOP_CHAT_VIEW";
    public static final String SHOP_CHAT_SEND = "SHOP_CHAT_SEND";
    public static final String SHOP_CHAT_MANAGE = "SHOP_CHAT_MANAGE";
    public static final String PRODUCT_CREATE = "PRODUCT_CREATE";
    public static final String PRODUCT_UPDATE = "PRODUCT_UPDATE";
    public static final String PRODUCT_DELETE = "PRODUCT_DELETE";
    public static final String ORDER_VIEW = "ORDER_VIEW";

    public static final List<String> SELLER_PERMISSIONS = List.of(
            SHOP_VIEW,
            SHOP_UPDATE,
            SHOP_STATUS_UPDATE,
            SHOP_SLUG_UPDATE,
            SHOP_DELETE,
            SHOP_EMPLOYEE_VIEW,
            SHOP_EMPLOYEE_MANAGE,
            SHOP_ROLE_VIEW,
            SHOP_ROLE_MANAGE,
            SHOP_CHAT_VIEW,
            SHOP_CHAT_SEND,
            SHOP_CHAT_MANAGE,
            PRODUCT_CREATE,
            PRODUCT_UPDATE,
            PRODUCT_DELETE,
            ORDER_VIEW
    );

    public static final List<String> BUYER_PERMISSIONS = List.of();

    public static final List<String> ALL_PERMISSIONS = SELLER_PERMISSIONS;
}
