package com.jashawn.inventory_api.reports;

import com.jashawn.inventory_api.department.dto.DepartmentDtoMapper;
import com.jashawn.inventory_api.reports.dto.*;
import com.jashawn.inventory_api.stockItem.dto.StockItemResponse;
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

    public Page<StockMovementResponse> movementHistory(DateRangeReportRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new DateTimeException("End date must be after start date");
        }

        return stockMovementRepository.movementHistoryWithinDateRange(
                request.startDate(),
                request.endDate(),
                request.toPageable()
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
                    request.toPageable()
                )
                .map(StockMovementDtoMapper::toDto);
    }

    public Page<DepartmentCostResponse> departmentCosts(DateRangeReportRequest request) {
        if (request.endDate().isBefore(request.startDate())) {
            throw new DateTimeException("End date must be after start date");
        }

        return stockMovementRepository.getDepartmentCostsByDateRange(
                request.startDate(),
                request.endDate(),
                request.toPageable()
        ).map(DepartmentDtoMapper::toCostResponse);
    }

    public Page<StockItemResponse> getStockItemsBetweenDateRange() {
        return null;
    }
}
