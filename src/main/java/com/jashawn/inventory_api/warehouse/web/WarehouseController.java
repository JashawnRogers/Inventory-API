package com.jashawn.inventory_api.warehouse.web;

import com.jashawn.inventory_api.warehouse.WarehouseService;
import com.jashawn.inventory_api.warehouse.dto.CreateWarehouseRequest;
import com.jashawn.inventory_api.warehouse.dto.UpdateWarehouseRequest;
import com.jashawn.inventory_api.warehouse.dto.WarehouseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Warehouses", description = "Create, retrieve, list, update, and soft-delete warehouses.")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Invalid warehouse field or page parameter.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "Warehouse was not found.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Warehouse state conflicts with the requested operation.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
})
public class WarehouseController {

    private final WarehouseService service;
    private final WarehouseAssembler assembler;

    public WarehouseController(WarehouseService service, WarehouseAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @PostMapping("/v1/warehouse")
    @Operation(summary = "Create a warehouse",
            description = "Creates a warehouse and returns its HATEOAS representation.")
    @ApiResponse(responseCode = "201", description = "Warehouse created.")
    public ResponseEntity<?> createWarehouse(@RequestBody CreateWarehouseRequest request) {
        EntityModel<WarehouseResponse> model = assembler.toModel(service.createWarehouse(request));

        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(model);
    }

    @GetMapping("/v1/warehouse/{id}")
    @Operation(summary = "Find a warehouse by ID",
            description = "Returns one non-deleted warehouse by UUID.")
    @ApiResponse(responseCode = "200", description = "Warehouse found.")
    public ResponseEntity<?> findWarehouse(@Parameter(description = "Warehouse UUID.") @PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(service.findWarehouse(id)));
    }

    @GetMapping("/v1/warehouses")
    @Operation(summary = "List warehouses",
            description = "Returns a paged HATEOAS collection of warehouses.")
    @ApiResponse(responseCode = "200", description = "Warehouse page returned.")
    public ResponseEntity<?> findAllWarehouses(
            @Parameter(description = "Zero-based page index used by Spring Data. Default currently configured by this controller is 1.")
            @RequestParam(required = false, defaultValue = "1") int page,
            @Parameter(description = "Page size.") @RequestParam(required = false, defaultValue = "25") int size,
            PagedResourcesAssembler<WarehouseResponse> pagedAssembler
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<WarehouseResponse> warehousePage = service.findAllWarehouses(pageRequest);
        PagedModel<EntityModel<WarehouseResponse>> pagedModel = pagedAssembler.toModel(warehousePage, assembler);

        return ResponseEntity.ok(pagedModel);
    }

    @PatchMapping("/v1/warehouse/{id}")
    @Operation(summary = "Update a warehouse",
            description = "Partially updates warehouse fields when supplied.")
    @ApiResponse(responseCode = "200", description = "Warehouse updated.")
    public ResponseEntity<?> updateWarehouse(@Parameter(description = "Warehouse UUID.") @PathVariable UUID id,
                                             @RequestBody UpdateWarehouseRequest request) {
        return ResponseEntity.ok(assembler.toModel(service.updateWarehouse(id, request)));
    }

    @DeleteMapping("/v1/warehouse/{id}")
    @Operation(summary = "Soft-delete a warehouse",
            description = "Marks the warehouse deleted without physically removing it from the database.")
    @ApiResponse(responseCode = "204", description = "Warehouse soft-deleted.", content = @Content)
    public ResponseEntity<?> deleteWarehouse(@Parameter(description = "Warehouse UUID.") @PathVariable UUID id) {
        service.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }
}
