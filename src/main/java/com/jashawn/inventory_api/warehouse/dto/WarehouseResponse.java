package com.jashawn.inventory_api.warehouse.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record WarehouseResponse(UUID id,
                                String name,
                                String location,
                                boolean active,
                                LocalDateTime createdAt,
                                LocalDateTime updatedAt) {
}
