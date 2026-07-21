package com.jashawn.inventory_api.stockItem.dto;

import com.jashawn.inventory_api.product.dto.ProductSummary;
import com.jashawn.inventory_api.stockItem.StockItem;
import com.jashawn.inventory_api.warehouse.dto.WarehouseSummary;

public class StockItemDtoMapper {

    public static StockItemResponse toDto(StockItem stockItem,
                                          ProductSummary product,
                                          WarehouseSummary warehouse) {
        return new StockItemResponse(
                stockItem.getId(),
                product,
                warehouse,
                stockItem.getQuantityOnHand(),
                stockItem.getReservedQuantity(),
                stockItem.getAvailableQuantity()
        );
    }
}
