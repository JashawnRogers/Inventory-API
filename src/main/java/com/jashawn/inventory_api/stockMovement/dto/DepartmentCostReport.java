package com.jashawn.inventory_api.stockMovement.dto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DepartmentCostReport {
    UUID getDepartmentId();
    String getDepartmentName();
    String getDepartmentCode();
    BigDecimal getTotalCost();
}
