package com.jashawn.inventory_api.inventory.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record IssueStockItemRequest(UUID productId,
                                    UUID warehouseId,
                                    UUID employeeId,
                                    UUID receivingDepartmentId,
                                    int quantity,
                                    String reason,
                                    String reference) {}
