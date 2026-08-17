package com.jashawn.inventory_api.product.web;

import com.jashawn.inventory_api.product.ProductService;
import com.jashawn.inventory_api.product.dto.CreateProductRequest;
import com.jashawn.inventory_api.product.dto.ProductResponse;
import com.jashawn.inventory_api.product.dto.UpdateProductRequest;
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

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Products", description = "Create, retrieve, filter, update, and soft-delete products.")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Invalid request field or page parameter.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "Product, category, or supplier was not found.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Duplicate product or inactive/invalid related resource.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
})
public class ProductController {

    private final ProductService service;
    private final ProductAssembler assembler;

    public ProductController(ProductService service, ProductAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @PostMapping("/v1/products")
    @Operation(summary = "Create a product",
            description = "Creates a product linked to an existing category and supplier, then returns the HATEOAS product representation.")
    @ApiResponse(responseCode = "201", description = "Product created.")
    public ResponseEntity<?> createProduct(@RequestBody CreateProductRequest request) {
        EntityModel<ProductResponse> model = assembler.toModel(service.createProduct(request));

        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(model);
    }

    @GetMapping("/v1/products/{id}")
    @Operation(summary = "Find a product by ID",
            description = "Returns one non-deleted product by its UUID.")
    @ApiResponse(responseCode = "200", description = "Product found.")
    public ResponseEntity<?> findProduct(@Parameter(description = "Product UUID.") @PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(service.findProduct(id)));
    }

    @GetMapping("/v1/products")
    @Operation(summary = "List products",
            description = "Returns a paged HATEOAS collection of non-deleted products filtered by status, name, SKU, or unit cost when provided.")
    @ApiResponse(responseCode = "200", description = "Product page returned.")
    public ResponseEntity<PagedModel<EntityModel<ProductResponse>>> findAllProducts(
            @Parameter(description = "Filter by active status.") @RequestParam(required = false) Boolean isActive,
            @Parameter(description = "Filter by product name.") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by SKU UUID.") @RequestParam(required = false) UUID sku,
            @Parameter(description = "Filter by exact unit cost.") @RequestParam(required = false) BigDecimal unitCost,
            @Parameter(description = "Zero-based page index used by Spring Data. Default currently configured by this controller is 1.")
            @RequestParam(required = false, defaultValue = "1") int page,
            @Parameter(description = "Page size.") @RequestParam(required = false, defaultValue = "25") int size,
            PagedResourcesAssembler<ProductResponse> pagedAssembler
    ) {
                PageRequest pageRequest = PageRequest.of(page, size);

                Page<ProductResponse> productPage = service.findAllProducts(isActive, name, sku, unitCost, pageRequest);
                PagedModel<EntityModel<ProductResponse>> pagedModel = pagedAssembler.toModel(productPage, assembler);

                return ResponseEntity.ok(pagedModel);
    }

    @PatchMapping("/v1/products/{id}")
    @Operation(summary = "Update a product",
            description = "Partially updates product fields and related category or supplier when those fields are supplied.")
    @ApiResponse(responseCode = "200", description = "Product updated.")
    public ResponseEntity<?> updateProduct(@Parameter(description = "Product UUID.") @PathVariable UUID id,
                                           @RequestBody UpdateProductRequest request) {
        EntityModel<ProductResponse> model = assembler.toModel(service.updateProduct(id, request));

        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/v1/products/{id}")
    @Operation(summary = "Soft-delete a product",
            description = "Marks the product deleted without physically removing it from the database.")
    @ApiResponse(responseCode = "204", description = "Product soft-deleted.", content = @Content)
    public ResponseEntity<?> softDeleteProduct(@Parameter(description = "Product UUID.") @PathVariable UUID id) {
        service.softDelete(id);

        return ResponseEntity.noContent().build();
    }
}
