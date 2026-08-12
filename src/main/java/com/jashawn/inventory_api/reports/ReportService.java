package com.jashawn.inventory_api.reports;

import com.jashawn.inventory_api.department.dto.DepartmentDtoMapper;
import com.jashawn.inventory_api.reports.dto.DepartmentCostRequest;
import com.jashawn.inventory_api.reports.dto.DepartmentCostResponse;
import com.jashawn.inventory_api.reports.dto.MovementHistoryByDepartmentRequest;
import com.jashawn.inventory_api.reports.dto.MovementHistoryRequest;
import com.jashawn.inventory_api.stockMovement.StockMovementRepository;
import com.jashawn.inventory_api.stockMovement.dto.StockMovementDtoMapper;
import com.jashawn.inventory_api.stockMovement.dto.StockMovementResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;

@Service
public class ReportService {

    private final StockMovementRepository stockMovementRepository;

    public ReportService(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }

    public Page<StockMovementResponse> movementHistory(MovementHistoryRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new DateTimeException("End date must be after start date");
        }

        return stockMovementRepository.movementHistoryWithinDateRange(
                request.startDate(),
                request.endDate(),
                request.toPageRequest()
        ).map(StockMovementDtoMapper::toDto);
    }

    public Page<StockMovementResponse> movementHistoryByDepartment(MovementHistoryByDepartmentRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new DateTimeException("End date must be after start date");
        }

        return stockMovementRepository.findByDepartment(
                    request.departmentId(),
                    request.startDate(),
                    request.endDate(),
                    request.toPageRequest()
                )
                .map(StockMovementDtoMapper::toDto);
    }

    public Page<DepartmentCostResponse> departmentCosts(DepartmentCostRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new DateTimeException("End date must be after start date");
        }

        return stockMovementRepository.getDepartmentCostsByDateRange(
                request.startDate(),
                request.endDate(),
                request.toPageRequest()
        ).map(DepartmentDtoMapper::toCostResponse);
    }
}
