package com.jashawn.inventory_api.reports.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record DepartmentCostResponse(
        UUID id,
        String departmentName,
        String departmentCode,
        BigDecimal totalCost
) {
}
