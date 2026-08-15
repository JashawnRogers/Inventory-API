package com.jashawn.inventory_api.stockItem.web;

import com.jashawn.inventory_api.stockItem.StockItemService;
import com.jashawn.inventory_api.stockItem.dto.StockItemResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stock-item")
public class StockItemController {

    private final StockItemService service;
    private final StockItemResponseAssembler assembler;

    public StockItemController(StockItemService service, StockItemResponseAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<StockItemResponse>> findStockItem(@PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(service.findStockItem(id)));
    }
}
