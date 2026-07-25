package com.jashawn.inventory_api.stockItem.dto;

import com.jashawn.inventory_api.product.dto.ProductSummary;
import com.jashawn.inventory_api.warehouse.dto.WarehouseSummary;

import java.util.UUID;

public record StockItemTransferResponse(UUID issuingStockItemId,
                                        ProductSummary product,
                                        WarehouseSummary issuingWarehouse,
                                        int issuingQuantityOnHand,
                                        int issuingReservedQuantity,
                                        int issuingAvailableQuantity,
                                        UUID receivingStockItemId,
                                        WarehouseSummary receivingWarehouse,
                                        int receivingQuantityOnHand,
                                        int receivingReservedQuantity,
                                        int receivingAvailableQuantity) {
}
