package com.jashawn.inventory_api.employee.web;

import com.jashawn.inventory_api.Exceptions.InvalidFieldException;
import com.jashawn.inventory_api.employee.EmployeeService;
import com.jashawn.inventory_api.employee.dto.CreateEmployeeRequest;
import com.jashawn.inventory_api.employee.dto.EmployeeResponse;
import com.jashawn.inventory_api.employee.dto.UpdateEmployeeRequest;
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
public class EmployeeController {

    private final EmployeeService service;
    private final EmployeeAssembler assembler;

    public EmployeeController(EmployeeService service, EmployeeAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @PostMapping("/v1/employees")
    public ResponseEntity<?> createEmployee(@RequestBody CreateEmployeeRequest request) {
        EntityModel<EmployeeResponse> model = assembler.toModel(service.createEmployee(request));

        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(model);
    }

    @GetMapping("/v1/employees/{id}")
    public ResponseEntity<?> findEmployee(@PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(service.findEmployee(id)));
    }

    @GetMapping("/v1/employees")
    public ResponseEntity<PagedModel<EntityModel<EmployeeResponse>>> findAllEmployees(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "25") int size,
            PagedResourcesAssembler<EmployeeResponse> pagedAssembler
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<EmployeeResponse> employeePage = service.findAllEmployees(firstName, lastName, email, active, pageRequest);
        PagedModel<EntityModel<EmployeeResponse>> pagedModel = pagedAssembler.toModel(employeePage, assembler);

        return ResponseEntity.ok(pagedModel);
    }

    @PatchMapping("/v1/employees/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable UUID id, @RequestBody UpdateEmployeeRequest request) {
        return ResponseEntity.ok(assembler.toModel(service.updateEmployee(id, request)));
    }

    @DeleteMapping("/v1/employees/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable UUID id) {
        service.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
