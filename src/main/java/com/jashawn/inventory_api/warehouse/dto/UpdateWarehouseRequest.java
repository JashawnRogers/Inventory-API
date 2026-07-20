package com.jashawn.inventory_api.warehouse.dto;

import java.util.UUID;

public record UpdateWarehouseRequest(String name,
                                     String location,
                                     Boolean active) {
}
