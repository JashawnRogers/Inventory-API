package com.jashawn.inventory_api.supplier.web;

import com.jashawn.inventory_api.supplier.SupplierService;
import com.jashawn.inventory_api.supplier.dto.CreateSupplierRequest;
import com.jashawn.inventory_api.supplier.dto.SupplierResponse;
import com.jashawn.inventory_api.supplier.dto.UpdateSupplierRequest;
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
public class SupplierController {

    private final SupplierService service;
    private final SupplierAssembler assembler;

    public SupplierController(SupplierService service, SupplierAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @PostMapping("/v1/suppliers")
    public ResponseEntity<?> createSupplier(@RequestBody CreateSupplierRequest request) {
        EntityModel<SupplierResponse> model = assembler.toModel(service.createSupplier(request));

        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(model);
    }

    @GetMapping("/v1/suppliers/{id}")
    public ResponseEntity<?> findSupplier(@PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(service.findSupplier(id)));
    }

    @GetMapping("/v1/suppliers")
    public ResponseEntity<PagedModel<EntityModel<SupplierResponse>>> findAllSuppliers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "25") int size,
            PagedResourcesAssembler<SupplierResponse> pagedAssembler

    ) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<SupplierResponse> supplierPage = service.findAllSuppliers(name, email, phone, status, pageRequest);
        PagedModel<EntityModel<SupplierResponse>> pagedModel = pagedAssembler.toModel(supplierPage, assembler);

        return ResponseEntity.ok(pagedModel);
    }

    @PatchMapping("/v1/suppliers/{id}")
    public ResponseEntity<?> updateSupplier(@PathVariable UUID id, @RequestBody UpdateSupplierRequest request) {
        return ResponseEntity.ok(assembler.toModel(service.updateSupplier(id, request)));
    }

    @DeleteMapping("/v1/suppliers/{id}")
    public ResponseEntity<?> softDelete(@PathVariable UUID id) {
        service.softDelete(id);
        return ResponseEntity.noContent().build();
    }
}
