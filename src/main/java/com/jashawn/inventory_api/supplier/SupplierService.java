package com.jashawn.inventory_api.supplier;

import com.jashawn.inventory_api.Exceptions.InvalidFieldException;
import com.jashawn.inventory_api.Exceptions.ResourceNotFoundException;
import com.jashawn.inventory_api.supplier.dto.CreateSupplierRequest;
import com.jashawn.inventory_api.supplier.dto.SupplierDtoMapper;
import com.jashawn.inventory_api.supplier.dto.SupplierResponse;
import com.jashawn.inventory_api.supplier.dto.UpdateSupplierRequest;
import com.jashawn.inventory_api.util.ValidationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Transactional
    public SupplierResponse createSupplier(CreateSupplierRequest request) {
        Supplier supplier = Supplier.create(request.name(), request.email(), request.phone());

        Supplier saved = supplierRepository.save(supplier);

        return SupplierDtoMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public SupplierResponse findSupplier(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "ID", id.toString()));

        return SupplierDtoMapper.toDto(supplier);
    }

    @Transactional(readOnly = true)
    public Page<SupplierResponse> findAllSuppliers(String name,
                                                   String email,
                                                   String phone,
                                                   Boolean status,
                                                   PageRequest pageRequest
    ) {
        if (phone != null && !ValidationUtils.isValidUSPhone(phone)) {
            throw new InvalidFieldException(phone, "Supplier", "phone");
        }

        // Email validation here too?

        Specification<Supplier> spec = Specification.where(SupplierSpecifications.isNotDeleted())
                .and(SupplierSpecifications.hasName(name))
                .or(SupplierSpecifications.hasEmail(email))
                .or(SupplierSpecifications.hasPhone(phone))
                .or(SupplierSpecifications.hasStatus(status));

        return supplierRepository.findAll(spec, pageRequest)
                .map(SupplierDtoMapper::toDto);
    }

    @Transactional
    public SupplierResponse updateSupplier(UUID id, UpdateSupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "ID", id.toString()));

        if (request.name() != null) {
            supplier.updateName(request.name());
        }

        if (request.phone() != null) {
            supplier.updatePhone(request.phone());
        }

        if (request.email() != null) {
            supplier.updateEmail(request.email());
        }

        if (request.status() != null) {
            if (request.status() && !supplier.isActive()) {
                supplier.activate();
            } else {
                supplier.deactivate();
            }
        }

        Supplier saved = supplierRepository.save(supplier);

        return SupplierDtoMapper.toDto(saved);
    }

    @Transactional
    public void softDelete(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "ID", id.toString()));

        supplier.softDelete();

        supplierRepository.save(supplier);
    }
}
