package com.jashawn.inventory_api.employee;

import com.jashawn.inventory_api.Exceptions.ResourceNotFoundException;
import com.jashawn.inventory_api.department.Department;
import com.jashawn.inventory_api.department.DepartmentRepository;
import com.jashawn.inventory_api.employee.dto.CreateEmployeeRequest;
import com.jashawn.inventory_api.employee.dto.EmployeeDtoMapper;
import com.jashawn.inventory_api.employee.dto.EmployeeResponse;
import com.jashawn.inventory_api.employee.dto.UpdateEmployeeRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "ID", request.departmentId().toString()));

        Employee employee = Employee.create(request.firstName(), request.lastName(), request.email(), department);

        Employee saved = employeeRepository.save(employee);

        return EmployeeDtoMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse findEmployee(UUID id) {
        return employeeRepository.findById(id)
                .map(EmployeeDtoMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "ID", id.toString()));
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> findAllEmployees(String firstName,
                                                   String lastName,
                                                   String email,
                                                   Boolean active,
                                                   PageRequest pageRequest) {
        Specification<Employee> spec = Specification.where(EmployeeSpecifications.isNotDeleted())
                .and(EmployeeSpecifications.hasFirstName(firstName))
                .or(EmployeeSpecifications.hasLastName(lastName))
                .or(EmployeeSpecifications.hasEmail(email))
                .or(EmployeeSpecifications.hasStatus(active));

        return employeeRepository.findAll(spec, pageRequest)
                .map(EmployeeDtoMapper::toDto);
    }

    @Transactional
    public EmployeeResponse updateEmployee(UUID id, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "ID", id.toString()));

        if (request.firstName() != null && !request.firstName().isBlank()) {
            employee.updateFirstName(request.firstName());
        }

        if (request.lastName() != null && !request.lastName().isBlank()) {
            employee.updateLastName(request.lastName());
        }

        if (request.email() != null && !request.email().isBlank()) {
            employee.updateEmail(request.email());
        }

        if (request.departmentId() != null) {
            Department department = departmentRepository.findById(request.departmentId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException("Department", "ID", request.departmentId().toString())
                            );
            employee.updateDepartment(department);
        }

        if (request.active() != null && request.active()) {
            employee.activate();
        }

        if (request.active() != null && !request.active()) {
            employee.deactivate();
        }

        Employee saved = employeeRepository.save(employee);

        return EmployeeDtoMapper.toDto(saved);
    }

    @Transactional
    public void deleteEmployee(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "ID", id.toString()));

        employee.softDelete();

        employeeRepository.save(employee);
    }

}
