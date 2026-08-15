package com.jashawn.inventory_api.inventory.web;

import com.jashawn.inventory_api.inventory.InventoryService;
import com.jashawn.inventory_api.inventory.dto.*;
import com.jashawn.inventory_api.stockItem.dto.StockItemResponse;
import com.jashawn.inventory_api.stockItem.dto.StockItemTransferResponse;
import com.jashawn.inventory_api.stockItem.web.StockItemResponseAssembler;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
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
    public ResponseEntity<EntityModel<StockItemResponse>> receive(@RequestBody ReceiveInventoryRequest request) {
        StockItemResponse response = service.receive(request);
        return ResponseEntity.ok(stockItemResponseAssembler.toModel(response));
    }

    @PostMapping("/issue")
    public ResponseEntity<EntityModel<StockItemResponse>> issue(@RequestBody @Valid IssueStockItemRequest request) {
        StockItemResponse response = service.issue(request);
        return ResponseEntity.ok(stockItemResponseAssembler.toModel(response));
    }

    @PostMapping("/reserve")
    public ResponseEntity<EntityModel<StockItemResponse>> reserve(@RequestBody @Valid ReserveStockItemRequest request) {
        StockItemResponse response = service.reserve(request);
        return ResponseEntity.ok(stockItemResponseAssembler.toModel(response));
    }

    @PostMapping("/release-reservation")
    public ResponseEntity<EntityModel<StockItemResponse>> releaseReservation
            (@RequestBody @Valid ReleaseReservationRequest request) {
        StockItemResponse response = service.releaseReservation(request);
        return ResponseEntity.ok(stockItemResponseAssembler.toModel(response));
    }

    @PostMapping("/increase-adjustment")
    public ResponseEntity<EntityModel<StockItemResponse>> increaseByAdjustment
            (@RequestBody @Valid ManualAdjustmentRequest request) {
        StockItemResponse response = service.increaseByAdjustment(request);
        return ResponseEntity.ok(stockItemResponseAssembler.toModel(response));
    }

    @PostMapping("/decrease-adjustment")
    public ResponseEntity<EntityModel<StockItemResponse>> decreaseByAdjustment
            (@RequestBody @Valid ManualAdjustmentRequest request) {
        StockItemResponse response = service.decreaseByAdjustment(request);
        return ResponseEntity.ok(stockItemResponseAssembler.toModel(response));
    }

    @PostMapping("/transfer")
    public ResponseEntity<EntityModel<StockItemTransferResponse>> transferBetweenWarehouses
            (@RequestBody @Valid WarehouseTransferRequest request) {
        StockItemTransferResponse response = service.transferBetweenWarehouses(request);
        return ResponseEntity.ok(stockItemTransferResponseAssembler.toModel(response));
    }
}
