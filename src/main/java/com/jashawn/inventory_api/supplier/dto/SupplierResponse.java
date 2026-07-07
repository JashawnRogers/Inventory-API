package com.jashawn.inventory_api.supplier.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SupplierResponse(
        UUID id,
        String name,
        String email,
        String phone,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
