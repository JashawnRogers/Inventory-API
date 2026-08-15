package com.jashawn.inventory_api.category.web;

import com.jashawn.inventory_api.category.CategoryService;
import com.jashawn.inventory_api.category.dto.CategoryResponse;
import com.jashawn.inventory_api.category.dto.CreateCategoryRequest;
import com.jashawn.inventory_api.category.dto.UpdateCategoryRequest;
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
public class CategoryController {

    private final CategoryService service;
    private final CategoryAssembler assembler;

    public CategoryController(CategoryService service, CategoryAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @PostMapping("/v1/categories")
    public ResponseEntity<?> createCategory(@RequestBody CreateCategoryRequest request) {
        EntityModel<CategoryResponse> model = assembler.toModel(service.createCategory(request));

        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(model);
    }

    @GetMapping("/v1/categories/{id}")
    public ResponseEntity<?> findCategory(@PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(service.findCategoryById(id)));
    }

    @GetMapping("/v1/categories")
    public ResponseEntity<PagedModel<EntityModel<CategoryResponse>>> findAlCategories(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean status,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "25") int size,
            PagedResourcesAssembler<CategoryResponse> pagedAssembler
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<CategoryResponse> categoryPage = service.findAllCategories(name, status, pageRequest);
        PagedModel<EntityModel<CategoryResponse>> pagedModel = pagedAssembler.toModel(categoryPage, assembler);

        return ResponseEntity.ok(pagedModel);
    }

    @PatchMapping("/v1/categories/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable UUID id, @RequestBody UpdateCategoryRequest request) {
        return ResponseEntity.ok(assembler.toModel(service.updateCategory(id, request)));
    }

    @DeleteMapping("/v1/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable UUID id) {
        service.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }


}
