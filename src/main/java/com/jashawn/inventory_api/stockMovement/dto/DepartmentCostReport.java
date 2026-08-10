package com.jashawn.inventory_api.stockMovement.dto;

import java.math.BigDecimal;

public interface DepartmentCostReport {
    String getDepartmentName();
    BigDecimal getTotalCost();
}
