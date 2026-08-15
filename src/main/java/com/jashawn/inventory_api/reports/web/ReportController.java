package com.jashawn.inventory_api.reports.web;

import com.jashawn.inventory_api.common.PageableCommand;
import com.jashawn.inventory_api.department.web.DepartmentCostResponseAssembler;
import com.jashawn.inventory_api.inventory.dto.InventoryValueByWarehouseResponse;
import com.jashawn.inventory_api.inventory.dto.InventoryValueResponse;
import com.jashawn.inventory_api.inventory.web.InventoryValueByWarehouseAssembler;
import com.jashawn.inventory_api.inventory.web.InventoryValueResponseAssembler;
import com.jashawn.inventory_api.reports.ReportService;
import com.jashawn.inventory_api.reports.dto.DateRangeReportRequest;
import com.jashawn.inventory_api.reports.dto.DepartmentCostResponse;
import com.jashawn.inventory_api.reports.dto.MovementHistoryByDepartmentRequest;
import com.jashawn.inventory_api.stockItem.dto.StockItemRequest;
import com.jashawn.inventory_api.stockItem.dto.StockItemResponse;
import com.jashawn.inventory_api.stockItem.web.StockItemResponseAssembler;
import com.jashawn.inventory_api.stockMovement.dto.StockMovementResponse;
import com.jashawn.inventory_api.stockMovement.web.StockMovementResponseAssembler;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;
    private final StockMovementResponseAssembler stockMovementResponseAssembler;
    private final DepartmentCostResponseAssembler departmentCostResponseAssembler;
    private final StockItemResponseAssembler stockItemResponseAssembler;
    private final InventoryValueByWarehouseAssembler inventoryValueByWarehouseAssembler;
    private final InventoryValueResponseAssembler inventoryValueResponseAssembler;

    public ReportController(ReportService reportService,
                            StockMovementResponseAssembler stockMovementResponseAssembler,
                            DepartmentCostResponseAssembler departmentCostResponseAssembler,
                            StockItemResponseAssembler stockItemResponseAssembler,
                            InventoryValueByWarehouseAssembler inventoryValueByWarehouseAssembler,
                            InventoryValueResponseAssembler inventoryValueResponseAssembler
    ) {
        this.reportService = reportService;
        this.stockMovementResponseAssembler = stockMovementResponseAssembler;
        this.departmentCostResponseAssembler = departmentCostResponseAssembler;
        this.stockItemResponseAssembler = stockItemResponseAssembler;
        this.inventoryValueByWarehouseAssembler = inventoryValueByWarehouseAssembler;
        this.inventoryValueResponseAssembler = inventoryValueResponseAssembler;
    }

    @GetMapping("/movement-history")
    public ResponseEntity<PagedModel<EntityModel<StockMovementResponse>>> movementHistory(
            @RequestBody @Valid DateRangeReportRequest request,
            PagedResourcesAssembler<StockMovementResponse> pagedAssembler
    ) {
        Page<StockMovementResponse> smPage = reportService.movementHistory(request);
        PagedModel<EntityModel<StockMovementResponse>> pagedModel = pagedAssembler.toModel(
                smPage, stockMovementResponseAssembler
        );

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/department/movement-history")
    public ResponseEntity<PagedModel<EntityModel<StockMovementResponse>>> movementHistoryByDepartment(
            @RequestBody @Valid MovementHistoryByDepartmentRequest request,
            PagedResourcesAssembler<StockMovementResponse> pagedAssembler
    ) {
       Page<StockMovementResponse> smPage = reportService.movementHistoryByDepartment(request);
       PagedModel<EntityModel<StockMovementResponse>> pagedModel = pagedAssembler.toModel(
               smPage, stockMovementResponseAssembler
       );

       return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/department/cost")
    public ResponseEntity<PagedModel<EntityModel<DepartmentCostResponse>>> departmentCost(
            @RequestBody @Valid DateRangeReportRequest request,
            PagedResourcesAssembler<DepartmentCostResponse> pagedAssembler
    ) {
        Page<DepartmentCostResponse> departmentCostPage = reportService.departmentCosts(request);
        PagedModel<EntityModel<DepartmentCostResponse>> pagedModel = pagedAssembler.toModel(
                departmentCostPage, departmentCostResponseAssembler
        );

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/stock-item")
    public ResponseEntity<PagedModel<EntityModel<StockItemResponse>>> stockItemsBetweenDateRange(
            @RequestBody @Valid DateRangeReportRequest request,
            PagedResourcesAssembler<StockItemResponse> pagedAssembler
    ) {
        Page<StockItemResponse> stockItemPage = reportService.getStockItemsBetweenDateRange(request);
        PagedModel<EntityModel<StockItemResponse>> pagedModel = pagedAssembler.toModel(
                stockItemPage, stockItemResponseAssembler
        );

        return ResponseEntity.ok(pagedModel);
    }

    @GetMapping("/inventory/value")
    public ResponseEntity<EntityModel<InventoryValueByWarehouseResponse>> inventoryValueForProductAndWarehouse(
            @RequestBody @Valid StockItemRequest request
    ) {
        return ResponseEntity.ok(inventoryValueByWarehouseAssembler.toModel(
                                reportService.getInventoryValueForProductAndWarehouse(request)
        ));
    }

    @GetMapping("/inventory/value/global")
    public ResponseEntity<PagedModel<EntityModel<InventoryValueResponse>>> globalInventoryValue(
            @RequestBody @Valid PageableCommand command,
            PagedResourcesAssembler<InventoryValueResponse> pagedAssembler
    ) {
        Page<InventoryValueResponse> inventoryValuePage = reportService.getGlobalInventoryValue(command);
        PagedModel<EntityModel<InventoryValueResponse>> pagedModel = pagedAssembler.toModel(
                inventoryValuePage, inventoryValueResponseAssembler
        );

        return ResponseEntity.ok(pagedModel);
    }
}
