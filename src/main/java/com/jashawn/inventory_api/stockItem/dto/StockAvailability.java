package com.jashawn.inventory_api.stockItem.dto;

import java.util.UUID;

public interface StockAvailability {
    UUID getProductId();
    String getProductName();
    Long getAvailableStock();
}
