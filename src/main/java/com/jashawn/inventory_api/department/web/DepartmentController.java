package com.jashawn.inventory_api.department.web;

import com.jashawn.inventory_api.department.DepartmentService;
import com.jashawn.inventory_api.department.dto.CreateDepartmentRequest;
import com.jashawn.inventory_api.department.dto.DepartmentResponse;
import com.jashawn.inventory_api.department.dto.UpdateDepartmentRequest;
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
@Tag(name = "Departments", description = "Create, retrieve, filter, update, and soft-delete departments.")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Invalid request field or page parameter.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "Department was not found.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Department state conflicts with the requested operation.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
})
public class DepartmentController {

    DepartmentService service;
    DepartmentAssembler assembler;

    public DepartmentController(DepartmentService service, DepartmentAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @PostMapping("/v1/departments")
    @Operation(summary = "Create a department",
            description = "Creates a department and returns its HATEOAS representation.")
    @ApiResponse(responseCode = "201", description = "Department created.")
    public ResponseEntity<?> createDepartment(@RequestBody CreateDepartmentRequest request) {
        EntityModel<DepartmentResponse> model = assembler.toModel(service.createDepartment(request));

        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(model);
    }

    @GetMapping("/v1/departments/{id}")
    @Operation(summary = "Find a department by ID",
            description = "Returns one non-deleted department by UUID.")
    @ApiResponse(responseCode = "200", description = "Department found.")
    public ResponseEntity<?> findDepartment(@Parameter(description = "Department UUID.") @PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(service.findDepartment(id)));
    }

    @GetMapping("/v1/departments")
    @Operation(summary = "List departments",
            description = "Returns a paged HATEOAS collection of non-deleted departments filtered by name, code, or active status when provided.")
    @ApiResponse(responseCode = "200", description = "Department page returned.")
    public ResponseEntity<PagedModel<EntityModel<DepartmentResponse>>> findAllDepartments(
            @Parameter(description = "Filter by department name.") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by department code.") @RequestParam(required = false) String code,
            @Parameter(description = "Filter by active status.") @RequestParam(required = false) Boolean active,
            @Parameter(description = "Zero-based page index used by Spring Data. Default currently configured by this controller is 1.")
            @RequestParam(required = false, defaultValue = "1") int page,
            @Parameter(description = "Page size.") @RequestParam(required = false, defaultValue = "25") int size,
            PagedResourcesAssembler<DepartmentResponse> pagedAssembler
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<DepartmentResponse> departmentPage = service.findAllDepartments(name, code, active, pageRequest);
        PagedModel<EntityModel<DepartmentResponse>> pagedModel = pagedAssembler.toModel(departmentPage, assembler);

        return ResponseEntity.ok(pagedModel);
    }

    @DeleteMapping("/v1/departments/{id}")
    @Operation(summary = "Soft-delete a department",
            description = "Marks the department deleted without physically removing it from the database.")
    @ApiResponse(responseCode = "204", description = "Department soft-deleted.", content = @Content)
    public ResponseEntity<?> deleteDepartment(@Parameter(description = "Department UUID.") @PathVariable UUID id) {
        service.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/v1/departments/{id}")
    @Operation(summary = "Update a department",
            description = "Partially updates department fields when supplied.")
    @ApiResponse(responseCode = "200", description = "Department updated.")
    public ResponseEntity<?> updateDepartment(@Parameter(description = "Department UUID.") @PathVariable UUID id,
                                              @RequestBody UpdateDepartmentRequest request) {
        return ResponseEntity.ok(assembler.toModel(service.updateDepartment(id, request)));
    }
}
