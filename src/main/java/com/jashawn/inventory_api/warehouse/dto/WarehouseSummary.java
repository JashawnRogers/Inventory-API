package com.jashawn.inventory_api.warehouse.dto;

import java.util.UUID;

public record WarehouseSummary(UUID id,
                               String name,
                               String location) {
}
