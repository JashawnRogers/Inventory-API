package com.jashawn.inventory_api.reports;

import com.jashawn.inventory_api.common.PageableCommand;
import com.jashawn.inventory_api.department.dto.DepartmentDtoMapper;
import com.jashawn.inventory_api.inventory.dto.InventoryValueByWarehouseResponse;
import com.jashawn.inventory_api.inventory.dto.InventoryValueResponse;
import com.jashawn.inventory_api.inventory.projection.InventoryValueByWarehouse;
import com.jashawn.inventory_api.product.dto.ProductDtoMapper;
import com.jashawn.inventory_api.reports.dto.*;
import com.jashawn.inventory_api.stockItem.StockItem;
import com.jashawn.inventory_api.stockItem.StockItemRepository;
import com.jashawn.inventory_api.stockItem.dto.*;
import com.jashawn.inventory_api.stockMovement.StockMovementRepository;
import com.jashawn.inventory_api.stockMovement.dto.StockMovementDtoMapper;
import com.jashawn.inventory_api.stockMovement.dto.StockMovementResponse;
import com.jashawn.inventory_api.warehouse.dto.WarehouseDtoMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.util.List;

@Service
public class ReportService {

    private final StockMovementRepository stockMovementRepository;
    private final StockItemRepository stockItemRepository;

    public ReportService(StockMovementRepository stockMovementRepository, StockItemRepository stockItemRepository) {
        this.stockMovementRepository = stockMovementRepository;
        this.stockItemRepository = stockItemRepository;
    }

    @Transactional(readOnly = true)
    public Page<StockMovementResponse> movementHistory(DateRangeReportRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new DateTimeException("End date must be after start date");
        }

        return stockMovementRepository.movementHistoryWithinDateRange(
                request.startDate(),
                request.endDate(),
                request.toPageable()
        ).map(StockMovementDtoMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<StockMovementResponse> movementHistoryByDepartment(MovementHistoryByDepartmentRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new DateTimeException("End date must be after start date");
        }

        return stockMovementRepository.findByDepartment(
                    request.departmentId(),
                    request.startDate(),
                    request.endDate(),
                    request.toPageable()
                )
                .map(StockMovementDtoMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<DepartmentCostResponse> departmentCosts(DateRangeReportRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new DateTimeException("End date must be after start date");
        }

        return stockMovementRepository.getDepartmentCostsByDateRange(
                request.startDate(),
                request.endDate(),
                request.toPageable()
        ).map(DepartmentDtoMapper::toCostResponse);
    }

    @Transactional(readOnly = true)
    public Page<StockItemResponse> getStockItemsBetweenDateRange(DateRangeReportRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new DateTimeException("End date must be after start date");
        }

        Page<StockItem> result = stockItemRepository.getStockItemsBetweenDateRange(
                request.startDate(),
                request.endDate(),
                request.toPageable()
        );

        return result.map(stockItem ->
             StockItemDtoMapper.toDto(
                    stockItem,
                    ProductDtoMapper.toSummaryDto(stockItem.getProduct()),
                    WarehouseDtoMapper.toSummaryDto(stockItem.getWarehouse()))
        );
    }

    @Transactional(readOnly = true)
    public List<StockAvailabilityResponse> getLowStockReport(PageableCommand command) {
        return stockItemRepository.getLowStockReport(command.toPageable()).stream()
                .map(stockItem -> new StockAvailabilityResponse(
                        stockItem.getProductId(),
                        stockItem.getProductName(),
                        stockItem.getAvailableStock())
                ).toList();
    }

    @Transactional(readOnly = true)
    public InventoryValueByWarehouseResponse getInventoryValueForProductAndWarehouse(StockItemRequest request) {
        InventoryValueByWarehouse result = stockItemRepository.getInventoryValueForProductAndWarehouse(
                request.productId(),
                request.warehouseId()
        );

        return new InventoryValueByWarehouseResponse(
                result.getProductName(),
                result.getProductId(),
                result.getInventoryValue(),
                result.getProductUnitCost(),
                result.getQuantityOnHand(),
                result.getWarehouseName(),
                result.getWarehouseId()
        );
    }

    @Transactional(readOnly = true)
    public List<InventoryValueResponse> getGlobalInventoryValue(PageableCommand command) {
        return stockItemRepository.getGlobalInventoryValue(command.toPageable()).stream()
                .map(stockItem -> new InventoryValueResponse(
                        stockItem.getProductName(),
                        stockItem.getProductId(),
                        stockItem.getQuantityOnHand(),
                        stockItem.getProductUnitCost(),
                        stockItem.getInventoryValue()
                        )
                ).toList();
    }
}
