package com.jashawn.inventory_api.warehouse.dto;

import com.jashawn.inventory_api.warehouse.Warehouse;

public class WarehouseDtoMapper {

    public static WarehouseResponse toDto(Warehouse warehouse) {
        return new WarehouseResponse(
                warehouse.getId(),
                warehouse.getName(),
                warehouse.getLocation(),
                warehouse.isActive(),
                warehouse.getCreatedAt(),
                warehouse.getUpdatedAt()
        );
    }

    public static WarehouseSummary toSummaryDto(Warehouse warehouse) {
        return new WarehouseSummary(warehouse.getId(), warehouse.getName(), warehouse.getLocation());
    }
}
