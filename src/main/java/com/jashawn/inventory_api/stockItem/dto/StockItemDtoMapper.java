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

    public static StockItemTransferResponse toTransferResponse(StockItem issuingStockItem,
                                                               ProductSummary product,
                                                               WarehouseSummary issuingWarehouse,
                                                               StockItem receivingStockItem,
                                                               WarehouseSummary receivingWarehouse) {
        return new StockItemTransferResponse(
                issuingStockItem.getId(),
                product,
                issuingWarehouse,
                issuingStockItem.getQuantityOnHand(),
                issuingStockItem.getReservedQuantity(),
                issuingStockItem.getAvailableQuantity(),
                receivingStockItem.getId(),
                receivingWarehouse,
                receivingStockItem.getQuantityOnHand(),
                receivingStockItem.getReservedQuantity(),
                receivingStockItem.getAvailableQuantity()
        );
    }
}
