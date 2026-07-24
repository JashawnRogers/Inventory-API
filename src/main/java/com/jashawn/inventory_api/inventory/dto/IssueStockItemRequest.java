package com.jashawn.inventory_api.inventory.dto;

import java.util.UUID;

public record IssueStockItemRequest(UUID productId,
                                    UUID warehouseId,
                                    UUID employeeId,
                                    UUID receivingDepartmentId,
                                    int quantity,
                                    String reason,
                                    String reference) {}
