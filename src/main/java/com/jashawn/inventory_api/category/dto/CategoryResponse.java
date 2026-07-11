package com.jashawn.inventory_api.category.dto;


import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String description,
        boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
