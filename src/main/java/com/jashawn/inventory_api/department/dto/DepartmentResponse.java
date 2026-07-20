package com.jashawn.inventory_api.department.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DepartmentResponse(UUID id,
                                 String name,
                                 String code,
                                 boolean active,
                                 LocalDateTime createdAt,
                                 LocalDateTime updatedAt,
                                 LocalDateTime deletedAt
) {
}
