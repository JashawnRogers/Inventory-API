package com.jashawn.inventory_api.employee.web;

import com.jashawn.inventory_api.employee.EmployeeService;
import com.jashawn.inventory_api.employee.dto.CreateEmployeeRequest;
import com.jashawn.inventory_api.employee.dto.EmployeeResponse;
import com.jashawn.inventory_api.employee.dto.UpdateEmployeeRequest;
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
@Tag(name = "Employees", description = "Create, retrieve, filter, update, and soft-delete employees.")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Invalid employee field or page parameter.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "404", description = "Employee or related department was not found.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "409", description = "Inactive department or employee state conflicts with the requested operation.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
        @ApiResponse(responseCode = "500", description = "Unexpected server error.",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
})
public class EmployeeController {

    private final EmployeeService service;
    private final EmployeeAssembler assembler;

    public EmployeeController(EmployeeService service, EmployeeAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @PostMapping("/v1/employees")
    @Operation(summary = "Create an employee",
            description = "Creates an employee assigned to an existing active department and returns the HATEOAS employee representation.")
    @ApiResponse(responseCode = "201", description = "Employee created.")
    public ResponseEntity<?> createEmployee(@RequestBody CreateEmployeeRequest request) {
        EntityModel<EmployeeResponse> model = assembler.toModel(service.createEmployee(request));

        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(model);
    }

    @GetMapping("/v1/employees/{id}")
    @Operation(summary = "Find an employee by ID",
            description = "Returns one non-deleted employee by UUID.")
    @ApiResponse(responseCode = "200", description = "Employee found.")
    public ResponseEntity<?> findEmployee(@Parameter(description = "Employee UUID.") @PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(service.findEmployee(id)));
    }

    @GetMapping("/v1/employees")
    @Operation(summary = "List employees",
            description = "Returns a paged HATEOAS collection of non-deleted employees filtered by name, email, or active status when provided.")
    @ApiResponse(responseCode = "200", description = "Employee page returned.")
    public ResponseEntity<PagedModel<EntityModel<EmployeeResponse>>> findAllEmployees(
            @Parameter(description = "Filter by employee first name.") @RequestParam(required = false) String firstName,
            @Parameter(description = "Filter by employee last name.") @RequestParam(required = false) String lastName,
            @Parameter(description = "Filter by employee email.") @RequestParam(required = false) String email,
            @Parameter(description = "Filter by active status.") @RequestParam(required = false) Boolean active,
            @Parameter(description = "Zero-based page index used by Spring Data. Default currently configured by this controller is 1.")
            @RequestParam(required = false, defaultValue = "1") int page,
            @Parameter(description = "Page size.") @RequestParam(required = false, defaultValue = "25") int size,
            PagedResourcesAssembler<EmployeeResponse> pagedAssembler
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<EmployeeResponse> employeePage = service.findAllEmployees(firstName, lastName, email, active, pageRequest);
        PagedModel<EntityModel<EmployeeResponse>> pagedModel = pagedAssembler.toModel(employeePage, assembler);

        return ResponseEntity.ok(pagedModel);
    }

    @PatchMapping("/v1/employees/{id}")
    @Operation(summary = "Update an employee",
            description = "Partially updates employee fields or department assignment when supplied.")
    @ApiResponse(responseCode = "200", description = "Employee updated.")
    public ResponseEntity<?> updateEmployee(@Parameter(description = "Employee UUID.") @PathVariable UUID id,
                                            @RequestBody UpdateEmployeeRequest request) {
        return ResponseEntity.ok(assembler.toModel(service.updateEmployee(id, request)));
    }

    @DeleteMapping("/v1/employees/{id}")
    @Operation(summary = "Soft-delete an employee",
            description = "Marks the employee deleted without physically removing it from the database.")
    @ApiResponse(responseCode = "204", description = "Employee soft-deleted.", content = @Content)
    public ResponseEntity<?> deleteEmployee(@Parameter(description = "Employee UUID.") @PathVariable UUID id) {
        service.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
