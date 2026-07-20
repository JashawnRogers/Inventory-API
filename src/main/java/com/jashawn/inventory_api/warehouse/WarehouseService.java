package com.jashawn.inventory_api.warehouse;

import com.jashawn.inventory_api.Exceptions.ResourceNotFoundException;
import com.jashawn.inventory_api.warehouse.dto.CreateWarehouseRequest;
import com.jashawn.inventory_api.warehouse.dto.UpdateWarehouseRequest;
import com.jashawn.inventory_api.warehouse.dto.WarehouseDtoMapper;
import com.jashawn.inventory_api.warehouse.dto.WarehouseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WarehouseService {

    private final WarehouseRepository repository;

    public WarehouseService(WarehouseRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public WarehouseResponse createWarehouse(CreateWarehouseRequest request) {
        Warehouse warehouse = Warehouse.create(request.name(), request.location());

        Warehouse saved = repository.save(warehouse);

        return WarehouseDtoMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public WarehouseResponse findWarehouse(UUID id) {
        return repository.findById(id)
                .map(WarehouseDtoMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "ID", id.toString()));
    }

    @Transactional(readOnly = true)
    public Page<WarehouseResponse> findAllWarehouses(PageRequest pageRequest) {
        return repository.findAll(pageRequest)
                .map(WarehouseDtoMapper::toDto);
    }

    @Transactional
    public WarehouseResponse updateWarehouse(UUID id, UpdateWarehouseRequest request) {
        Warehouse warehouse = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "ID", id.toString()));

        if (request.name() != null && !request.name().isBlank()) {
            warehouse.updateName(request.name());
        }

        if (request.location() != null && !request.location().isBlank()) {
            warehouse.updateLocation(request.location());
        }

        if (request.active() != null && request.active()) {
            warehouse.activate();
        }

        if (request.active() != null && !request.active()) {
            warehouse.deactivate();
        }

        Warehouse saved = repository.save(warehouse);

        return WarehouseDtoMapper.toDto(saved);
    }

    @Transactional
    public void deleteWarehouse(UUID id) {
        Warehouse warehouse = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "ID", id.toString()));

        warehouse.softDelete();

        repository.save(warehouse);
    }
}
