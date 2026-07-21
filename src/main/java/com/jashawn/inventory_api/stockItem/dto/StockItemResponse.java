package com.jashawn.inventory_api.stockItem.dto;

import com.jashawn.inventory_api.product.dto.ProductSummary;
import com.jashawn.inventory_api.warehouse.dto.WarehouseSummary;

import java.util.UUID;

public record StockItemResponse(UUID id,
                                ProductSummary product,
                                WarehouseSummary warehouse,
                                int quantityOnHand,
                                int reservedQuantity,
                                int availableQuantity) {
}
