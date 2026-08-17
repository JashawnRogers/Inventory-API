package com.jashawn.inventory_api.supplier.web;

import com.jashawn.inventory_api.supplier.SupplierService;
import com.jashawn.inventory_api.supplier.dto.CreateSupplierRequest;
import com.jashawn.inventory_api.supplier.dto.SupplierResponse;
import com.jashawn.inventory_api.supplier.dto.UpdateSupplierRequest;
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
@Tag(name = "Suppliers", description = "Create, retrieve, filter, update, and soft-delete suppliers.")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Invalid supplier field or page parameter.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "Supplier was not found.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Supplier state conflicts with the requested operation.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
})
public class SupplierController {

    private final SupplierService service;
    private final SupplierAssembler assembler;

    public SupplierController(SupplierService service, SupplierAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @PostMapping("/v1/suppliers")
    @Operation(summary = "Create a supplier",
            description = "Creates a supplier and returns its HATEOAS representation.")
    @ApiResponse(responseCode = "201", description = "Supplier created.")
    public ResponseEntity<?> createSupplier(@RequestBody CreateSupplierRequest request) {
        EntityModel<SupplierResponse> model = assembler.toModel(service.createSupplier(request));

        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(model);
    }

    @GetMapping("/v1/suppliers/{id}")
    @Operation(summary = "Find a supplier by ID",
            description = "Returns one non-deleted supplier by UUID.")
    @ApiResponse(responseCode = "200", description = "Supplier found.")
    public ResponseEntity<?> findSupplier(@Parameter(description = "Supplier UUID.") @PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(service.findSupplier(id)));
    }

    @GetMapping("/v1/suppliers")
    @Operation(summary = "List suppliers",
            description = "Returns a paged HATEOAS collection of non-deleted suppliers filtered by name, email, phone, or status when provided.")
    @ApiResponse(responseCode = "200", description = "Supplier page returned.")
    public ResponseEntity<PagedModel<EntityModel<SupplierResponse>>> findAllSuppliers(
            @Parameter(description = "Filter by supplier name.") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by supplier email.") @RequestParam(required = false) String email,
            @Parameter(description = "Filter by supplier phone number.") @RequestParam(required = false) String phone,
            @Parameter(description = "Filter by active status.") @RequestParam(required = false) Boolean status,
            @Parameter(description = "Zero-based page index used by Spring Data. Default currently configured by this controller is 1.")
            @RequestParam(required = false, defaultValue = "1") int page,
            @Parameter(description = "Page size.") @RequestParam(required = false, defaultValue = "25") int size,
            PagedResourcesAssembler<SupplierResponse> pagedAssembler

    ) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<SupplierResponse> supplierPage = service.findAllSuppliers(name, email, phone, status, pageRequest);
        PagedModel<EntityModel<SupplierResponse>> pagedModel = pagedAssembler.toModel(supplierPage, assembler);

        return ResponseEntity.ok(pagedModel);
    }

    @PatchMapping("/v1/suppliers/{id}")
    @Operation(summary = "Update a supplier",
            description = "Partially updates supplier fields when supplied.")
    @ApiResponse(responseCode = "200", description = "Supplier updated.")
    public ResponseEntity<?> updateSupplier(@Parameter(description = "Supplier UUID.") @PathVariable UUID id,
                                            @RequestBody UpdateSupplierRequest request) {
        return ResponseEntity.ok(assembler.toModel(service.updateSupplier(id, request)));
    }

    @DeleteMapping("/v1/suppliers/{id}")
    @Operation(summary = "Soft-delete a supplier",
            description = "Marks the supplier deleted without physically removing it from the database.")
    @ApiResponse(responseCode = "204", description = "Supplier soft-deleted.", content = @Content)
    public ResponseEntity<?> softDelete(@Parameter(description = "Supplier UUID.") @PathVariable UUID id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
