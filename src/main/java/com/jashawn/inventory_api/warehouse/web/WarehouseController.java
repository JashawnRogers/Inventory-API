package com.jashawn.inventory_api.warehouse.web;

import com.jashawn.inventory_api.warehouse.Warehouse;
import com.jashawn.inventory_api.warehouse.WarehouseService;
import com.jashawn.inventory_api.warehouse.dto.CreateWarehouseRequest;
import com.jashawn.inventory_api.warehouse.dto.UpdateWarehouseRequest;
import com.jashawn.inventory_api.warehouse.dto.WarehouseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class WarehouseController {

    private final WarehouseService service;
    private final WarehouseAssembler assembler;

    public WarehouseController(WarehouseService service, WarehouseAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @PostMapping("/v1/warehouse")
    public ResponseEntity<?> createWarehouse(@RequestBody CreateWarehouseRequest request) {
        EntityModel<WarehouseResponse> model = assembler.toModel(service.createWarehouse(request));

        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(model);
    }

    @GetMapping("/v1/warehouse/{id}")
    public ResponseEntity<?> findWarehouse(@PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(service.findWarehouse(id)));
    }

    @GetMapping("/v1/warehouses")
    public ResponseEntity<?> findAllWarehouses(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "25") int size,
            PagedResourcesAssembler<WarehouseResponse> pagedAssembler
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<WarehouseResponse> warehousePage = service.findAllWarehouses(pageRequest);
        PagedModel<EntityModel<WarehouseResponse>> pagedModel = pagedAssembler.toModel(warehousePage, assembler);

        return ResponseEntity.ok(pagedModel);
    }

    @PatchMapping("/v1/warehouse/{id}")
    public ResponseEntity<?> updateWarehouse(@PathVariable UUID id, @RequestBody UpdateWarehouseRequest request) {
        return ResponseEntity.ok(assembler.toModel(service.updateWarehouse(id, request)));
    }

    @DeleteMapping("/v1/warehouse/{id}")
    public ResponseEntity<?> deleteWarehouse(@PathVariable UUID id) {
        service.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }
}
