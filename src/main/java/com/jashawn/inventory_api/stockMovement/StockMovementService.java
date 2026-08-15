package com.jashawn.inventory_api.stockMovement;

import com.jashawn.inventory_api.Exceptions.ResourceNotFoundException;
import com.jashawn.inventory_api.stockItem.dto.StockItemResponse;
import com.jashawn.inventory_api.stockMovement.dto.StockMovementDtoMapper;
import com.jashawn.inventory_api.stockMovement.dto.StockMovementResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
public class StockMovementService {

    private final StockMovementRepository repository;

    public StockMovementService(StockMovementRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public StockMovementResponse findStockMovementById(UUID id) {
        return repository.findById(id)
                .map(StockMovementDtoMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Stock movement", "ID", id.toString()));
    }
}
