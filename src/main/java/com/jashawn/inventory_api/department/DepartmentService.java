package com.jashawn.inventory_api.department;

import com.jashawn.inventory_api.Exceptions.ResourceNotFoundException;
import com.jashawn.inventory_api.department.dto.CreateDepartmentRequest;
import com.jashawn.inventory_api.department.dto.DepartmentDtoMapper;
import com.jashawn.inventory_api.department.dto.DepartmentResponse;
import com.jashawn.inventory_api.department.dto.UpdateDepartmentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public DepartmentResponse createDepartment(CreateDepartmentRequest request) {
        Department department = Department.create(request.name(), request.code());

        Department saved = departmentRepository.save(department);

        return DepartmentDtoMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public DepartmentResponse findDepartment(UUID id) {
        return departmentRepository.findById(id)
                .map(DepartmentDtoMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "ID", id.toString()));
    }

    @Transactional(readOnly = true)
    public Page<DepartmentResponse> findAllDepartments(String name, String code, Boolean active, PageRequest pageRequest) {
        Specification<Department> spec = Specification.where(DepartmentSpecifications.isNotDeleted())
                .and(DepartmentSpecifications.hasName(name))
                .or(DepartmentSpecifications.hasCode(code))
                .or(DepartmentSpecifications.hasStatus(active));

        return departmentRepository.findAll(spec, pageRequest)
                .map(DepartmentDtoMapper::toDto);
    }

    @Transactional
    public DepartmentResponse updateDepartment(UUID id, UpdateDepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "ID", id.toString()));

        if (request.name() != null && !request.name().isBlank()) {
            department.updateName(request.name());
        }

        if (request.code() != null && !request.code().isBlank()) {
            department.updateCode(request.code());
        }

        if (request.active() != null && request.active()) {
            department.activate();
        }

        if (request.active() != null && !request.active()) {
            department.deactivate();
        }

        Department saved = departmentRepository.save(department);

        return DepartmentDtoMapper.toDto(saved);
    }

    @Transactional
    public void deleteDepartment(UUID id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "ID", id.toString()));

        department.softDelete();

        departmentRepository.save(department);
    }
}
