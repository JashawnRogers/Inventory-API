package com.jashawn.inventory_api.inventory.web;

import com.jashawn.inventory_api.inventory.InventoryService;
import com.jashawn.inventory_api.inventory.dto.*;
import com.jashawn.inventory_api.stockItem.dto.StockItemResponse;
import com.jashawn.inventory_api.stockItem.dto.StockItemTransferResponse;
import com.jashawn.inventory_api.stockItem.web.StockItemResponseAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory Operations", description = "Receive, issue, reserve, adjust, and transfer inventory.")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Invalid request field, invalid stock movement, or business rule violation.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "Referenced product, warehouse, employee, department, or stock item was not found.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Inactive related resource, invalid state, or insufficient available stock.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
})
public class InventoryController {

    private final InventoryService service;
    private final StockItemResponseAssembler stockItemResponseAssembler;
    private final StockItemTransferResponseAssembler stockItemTransferResponseAssembler;

    public InventoryController(InventoryService service,
                               StockItemResponseAssembler stockItemResponseAssembler,
                               StockItemTransferResponseAssembler stockItemTransferResponseAssembler) {
        this.service = service;
        this.stockItemResponseAssembler = stockItemResponseAssembler;
        this.stockItemTransferResponseAssembler = stockItemTransferResponseAssembler;
    }


    @PostMapping("/receive")
    @Operation(summary = "Receive inventory",
            description = "Adds received quantity to an existing product/warehouse stock item or creates one when missing, then records a RECEIVE stock movement.")
    @ApiResponse(responseCode = "200", description = "Inventory received and stock item returned.")
    public ResponseEntity<EntityModel<StockItemResponse>> receive(@RequestBody ReceiveInventoryRequest request) {
        StockItemResponse response = service.receive(request);
        return ResponseEntity.ok(stockItemResponseAssembler.toModel(response));
    }

    @PostMapping("/issue")
    @Operation(summary = "Issue inventory",
            description = "Subtracts available quantity from a stock item for a receiving department, records an ISSUE stock movement, and checks whether the product is now low stock.")
    @ApiResponse(responseCode = "200", description = "Inventory issued and stock item returned.")
    public ResponseEntity<EntityModel<StockItemResponse>> issue(@RequestBody @Valid IssueStockItemRequest request) {
        StockItemResponse response = service.issue(request);
        return ResponseEntity.ok(stockItemResponseAssembler.toModel(response));
    }

    @PostMapping("/reserve")
    @Operation(summary = "Reserve inventory",
            description = "Moves available stock into reserved quantity for a department, records a RESERVE stock movement, and checks whether the product is now low stock.")
    @ApiResponse(responseCode = "200", description = "Inventory reserved and stock item returned.")
    public ResponseEntity<EntityModel<StockItemResponse>> reserve(@RequestBody @Valid ReserveStockItemRequest request) {
        StockItemResponse response = service.reserve(request);
        return ResponseEntity.ok(stockItemResponseAssembler.toModel(response));
    }

    @PostMapping("/release-reservation")
    @Operation(summary = "Release reserved inventory",
            description = "Moves reserved quantity back into available stock and records a RELEASE_RESERVATION stock movement.")
    @ApiResponse(responseCode = "200", description = "Reservation released and stock item returned.")
    public ResponseEntity<EntityModel<StockItemResponse>> releaseReservation
            (@RequestBody @Valid ReleaseReservationRequest request) {
        StockItemResponse response = service.releaseReservation(request);
        return ResponseEntity.ok(stockItemResponseAssembler.toModel(response));
    }

    @PostMapping("/increase-adjustment")
    @Operation(summary = "Increase inventory by manual adjustment",
            description = "Increases stock item quantity and records an INCREASE_ADJUSTMENT stock movement. A department may be included for context.")
    @ApiResponse(responseCode = "200", description = "Inventory increased and stock item returned.")
    public ResponseEntity<EntityModel<StockItemResponse>> increaseByAdjustment
            (@RequestBody @Valid ManualAdjustmentRequest request) {
        StockItemResponse response = service.increaseByAdjustment(request);
        return ResponseEntity.ok(stockItemResponseAssembler.toModel(response));
    }

    @PostMapping("/decrease-adjustment")
    @Operation(summary = "Decrease inventory by manual adjustment",
            description = "Decreases stock item quantity, records a DECREASE_ADJUSTMENT stock movement, and checks whether the product is now low stock. A department may be included for context.")
    @ApiResponse(responseCode = "200", description = "Inventory decreased and stock item returned.")
    public ResponseEntity<EntityModel<StockItemResponse>> decreaseByAdjustment
            (@RequestBody @Valid ManualAdjustmentRequest request) {
        StockItemResponse response = service.decreaseByAdjustment(request);
        return ResponseEntity.ok(stockItemResponseAssembler.toModel(response));
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer inventory between warehouses",
            description = "Issues quantity from one warehouse stock item, receives it into another warehouse stock item, records transfer-out and transfer-in movements, and checks whether the product is now low stock.")
    @ApiResponse(responseCode = "200", description = "Inventory transferred and both affected stock items returned.")
    public ResponseEntity<EntityModel<StockItemTransferResponse>> transferBetweenWarehouses
            (@RequestBody @Valid WarehouseTransferRequest request) {
        StockItemTransferResponse response = service.transferBetweenWarehouses(request);
        return ResponseEntity.ok(stockItemTransferResponseAssembler.toModel(response));
    }
}
