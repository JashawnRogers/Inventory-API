package com.jashawn.inventory_api.stockMovement.web;

import com.jashawn.inventory_api.stockMovement.StockMovementService;
import com.jashawn.inventory_api.stockMovement.dto.StockMovementResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/movement")
public class StockMovementController {

    private final StockMovementService service;
    private final StockMovementResponseAssembler assembler;

    public StockMovementController(StockMovementService service, StockMovementResponseAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<StockMovementResponse>> findStockMovementById(@PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(service.findStockMovementById(id)));
    }
}
