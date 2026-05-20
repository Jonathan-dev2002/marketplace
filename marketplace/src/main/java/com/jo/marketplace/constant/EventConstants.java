package com.jo.marketplace.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EventConstants {

    public static final String SHOP = "Shop";
    public static final String SHOP_CREATED = "ShopCreated";
    public static final String SHOP_RESOURCE_NAME = "SHOP";
    public static final String SHOP_CREATED_ACTION = "SHOP_CREATED";
    public static final String SOURCE_KAFKA_CONSUMER = "kafka-consumer";

    public static final String HEADER_EVENT_ID = "event_id";
    public static final String HEADER_EVENT_TYPE = "event_type";
    public static final String HEADER_AGGREGATE_TYPE = "aggregate_type";
    public static final String HEADER_AGGREGATE_ID = "aggregate_id";
}
