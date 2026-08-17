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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports", description = "Read-only inventory and stock movement reporting endpoints.")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Malformed request body or invalid pageable values.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error. The current global handler also returns this for an invalid date range.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
})
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
    @Operation(summary = "Report stock movement history",
            description = "Returns a paged HATEOAS report of stock movements whose movement dates fall within the requested date range.")
    @ApiResponse(responseCode = "200", description = "Movement history page returned.")
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
    @Operation(summary = "Report stock movement history for a department",
            description = "Returns a paged HATEOAS report of stock movements for one department within the requested date range.")
    @ApiResponse(responseCode = "200", description = "Department movement history page returned.")
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
    @Operation(summary = "Report department costs",
            description = "Returns a paged HATEOAS report of aggregated stock movement costs by department within the requested date range.")
    @ApiResponse(responseCode = "200", description = "Department cost page returned.")
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
    @Operation(summary = "Report stock items created in a date range",
            description = "Returns a paged HATEOAS report of stock items whose creation dates fall within the requested date range.")
    @ApiResponse(responseCode = "200", description = "Stock item report page returned.")
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
    @Operation(summary = "Report inventory value for one product and warehouse",
            description = "Returns inventory value for the requested product/warehouse pair using quantity on hand and product unit cost.")
    @ApiResponse(responseCode = "200", description = "Inventory value returned.")
    public ResponseEntity<EntityModel<InventoryValueByWarehouseResponse>> inventoryValueForProductAndWarehouse(
            @RequestBody @Valid StockItemRequest request
    ) {
        return ResponseEntity.ok(inventoryValueByWarehouseAssembler.toModel(
                                reportService.getInventoryValueForProductAndWarehouse(request)
        ));
    }

    @GetMapping("/inventory/value/global")
    @Operation(summary = "Report global inventory value",
            description = "Returns a paged HATEOAS report of aggregated inventory value across products.")
    @ApiResponse(responseCode = "200", description = "Global inventory value page returned.")
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
