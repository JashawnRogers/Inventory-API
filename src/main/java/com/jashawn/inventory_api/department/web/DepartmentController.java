package com.jashawn.inventory_api.department.web;

import com.jashawn.inventory_api.department.DepartmentService;
import com.jashawn.inventory_api.department.dto.CreateDepartmentRequest;
import com.jashawn.inventory_api.department.dto.DepartmentResponse;
import com.jashawn.inventory_api.department.dto.UpdateDepartmentRequest;
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
public class DepartmentController {

    DepartmentService service;
    DepartmentAssembler assembler;

    public DepartmentController(DepartmentService service, DepartmentAssembler assembler) {
        this.service = service;
        this.assembler = assembler;
    }

    @PostMapping("/v1/departments")
    public ResponseEntity<?> createDepartment(@RequestBody CreateDepartmentRequest request) {
        EntityModel<DepartmentResponse> model = assembler.toModel(service.createDepartment(request));

        return ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(model);
    }

    @GetMapping("/v1/departments/{id}")
    public ResponseEntity<?> findDepartment(@PathVariable UUID id) {
        return ResponseEntity.ok(assembler.toModel(service.findDepartment(id)));
    }

    @GetMapping("/v1/departments")
    public ResponseEntity<PagedModel<EntityModel<DepartmentResponse>>> findAllDepartments(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "25") int size,
            PagedResourcesAssembler<DepartmentResponse> pagedAssembler
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<DepartmentResponse> departmentPage = service.findAllDepartments(name, code, active, pageRequest);
        PagedModel<EntityModel<DepartmentResponse>> pagedModel = pagedAssembler.toModel(departmentPage, assembler);

        return ResponseEntity.ok(pagedModel);
    }

    @DeleteMapping("/v1/departments/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable UUID id) {
        service.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/v1/departments/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable UUID id, @RequestBody UpdateDepartmentRequest request) {
        return ResponseEntity.ok(assembler.toModel(service.updateDepartment(id, request)));
    }
}
