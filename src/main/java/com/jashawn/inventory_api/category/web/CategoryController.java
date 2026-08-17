package com.jashawn.inventory_api.category.web;

import com.jashawn.inventory_api.category.CategoryService;
import com.jashawn.inventory_api.category.dto.CategoryResponse;
import com.jashawn.inventory_api.category.dto.CreateCategoryRequest;
import com.jashawn.inventory_api.category.dto.UpdateCategoryRequest;
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
@Tag(name = "Categories", description = "Create, retrieve, filter, update, and soft-delete product categories.")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Invalid request field or page parameter.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "Category was not found.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Category state conflicts with the requested operation.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
})
public class CategoryController {

    private final CategoryService service;
    private final CategoryAssembler assembler;

    public CategoryController(CategoryService service, CategoryAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @PostMapping("/v1/categories")
    @Operation(summary = "Create a category",
            description = "Creates a product category and returns its HATEOAS representation.")
    @ApiResponse(responseCode = "201", description = "Category created.")
    public ResponseEntity<?> createCategory(@RequestBody CreateCategoryRequest request) {
        EntityModel<CategoryResponse> model = assembler.toModel(service.createCategory(request));

        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(model);
    }

    @GetMapping("/v1/categories/{id}")
    @Operation(summary = "Find a category by ID",
            description = "Returns one non-deleted category by UUID.")
    @ApiResponse(responseCode = "200", description = "Category found.")
    public ResponseEntity<?> findCategory(@Parameter(description = "Category UUID.") @PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(service.findCategoryById(id)));
    }

    @GetMapping("/v1/categories")
    @Operation(summary = "List categories",
            description = "Returns a paged HATEOAS collection of non-deleted categories filtered by name or status when provided.")
    @ApiResponse(responseCode = "200", description = "Category page returned.")
    public ResponseEntity<PagedModel<EntityModel<CategoryResponse>>> findAlCategories(
            @Parameter(description = "Filter by category name.") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by active status.") @RequestParam(required = false) Boolean status,
            @Parameter(description = "Zero-based page index used by Spring Data. Default currently configured by this controller is 1.")
            @RequestParam(required = false, defaultValue = "1") int page,
            @Parameter(description = "Page size.") @RequestParam(required = false, defaultValue = "25") int size,
            PagedResourcesAssembler<CategoryResponse> pagedAssembler
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<CategoryResponse> categoryPage = service.findAllCategories(name, status, pageRequest);
        PagedModel<EntityModel<CategoryResponse>> pagedModel = pagedAssembler.toModel(categoryPage, assembler);

        return ResponseEntity.ok(pagedModel);
    }

    @PatchMapping("/v1/categories/{id}")
    @Operation(summary = "Update a category",
            description = "Partially updates category fields when supplied.")
    @ApiResponse(responseCode = "200", description = "Category updated.")
    public ResponseEntity<?> updateCategory(@Parameter(description = "Category UUID.") @PathVariable UUID id,
                                            @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(assembler.toModel(service.updateCategory(id, request)));
    }

    @DeleteMapping("/v1/categories/{id}")
    @Operation(summary = "Soft-delete a category",
            description = "Marks the category deleted without physically removing it from the database.")
    @ApiResponse(responseCode = "204", description = "Category soft-deleted.", content = @Content)
    public ResponseEntity<?> deleteCategory(@Parameter(description = "Category UUID.") @PathVariable UUID id) {
        service.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }


}
