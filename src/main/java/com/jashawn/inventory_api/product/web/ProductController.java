package com.jashawn.inventory_api.product.web;

import com.jashawn.inventory_api.product.ProductService;
import com.jashawn.inventory_api.product.dto.CreateProductRequest;
import com.jashawn.inventory_api.product.dto.ProductResponse;
import com.jashawn.inventory_api.product.dto.UpdateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService service;
    private final ProductAssembler assembler;

    public ProductController(ProductService service, ProductAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @PostMapping("/v1/products")
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequest request) {
        EntityModel<ProductResponse> model = assembler.toModel(service.createProduct(request));

        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(model);
    }

    @GetMapping("/v1/products/{id}")
    public ResponseEntity<?> findProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(service.findProduct(id)));
    }

    @GetMapping("/v1/products")
    public ResponseEntity<PagedModel<EntityModel<ProductResponse>>> findAllProducts(
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UUID sku,
            @RequestParam(required = false) BigDecimal unitCost,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "25") int size,
            PagedResourcesAssembler<ProductResponse> pagedAssembler
    ) {
                PageRequest pageRequest = PageRequest.of(page, size);

                Page<ProductResponse> productPage = service.findAllProducts(isActive, name, sku, unitCost, pageRequest);
                PagedModel<EntityModel<ProductResponse>> pagedModel = pagedAssembler.toModel(productPage, assembler);

                return ResponseEntity.ok(pagedModel);
    }

    @PatchMapping("/v1/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable UUID id, @RequestBody UpdateProductRequest request) {
        EntityModel<ProductResponse> model = assembler.toModel(service.updateProduct(id, request));

        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/v1/products/{id}")
    public ResponseEntity<?> softDeleteProduct(@PathVariable UUID id) {
        service.softDelete(id);

        return ResponseEntity.noContent().build();
    }
}
