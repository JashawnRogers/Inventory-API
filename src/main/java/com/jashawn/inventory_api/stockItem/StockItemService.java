package com.jashawn.inventory_api.stockItem;

import com.jashawn.inventory_api.Exceptions.ResourceNotFoundException;
import com.jashawn.inventory_api.product.Product;
import com.jashawn.inventory_api.product.ProductRepository;
import com.jashawn.inventory_api.product.dto.ProductDtoMapper;
import com.jashawn.inventory_api.stockItem.dto.StockItemDtoMapper;
import com.jashawn.inventory_api.stockItem.dto.StockItemResponse;
import com.jashawn.inventory_api.warehouse.Warehouse;
import com.jashawn.inventory_api.warehouse.WarehouseRepository;
import com.jashawn.inventory_api.warehouse.dto.WarehouseDtoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class StockItemService {

    private final StockItemRepository stockItemRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    public StockItemService(
            StockItemRepository stockItemRepository,
            ProductRepository productRepository,
            WarehouseRepository warehouseRepository
    ) {
        this.stockItemRepository = stockItemRepository;
        this.productRepository = productRepository;
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional(readOnly = true)
    public StockItemResponse findStockItem(UUID id) {
        StockItem stockItem = stockItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock Item", "ID", id.toString()));

        Product product = productRepository.findById(stockItem.getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", stockItem.getProduct().getId().toString()));

        Warehouse warehouse = warehouseRepository.findById(stockItem.getWarehouse().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "ID", stockItem.getWarehouse().getId().toString()));

        return StockItemDtoMapper.toDto(
                stockItem,
                ProductDtoMapper.toSummaryDto(product),
                WarehouseDtoMapper.toSummaryDto(warehouse)
        );
    }

}
