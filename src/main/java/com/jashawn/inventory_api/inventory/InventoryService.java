package com.jashawn.inventory_api.inventory;

import com.jashawn.inventory_api.Exceptions.BusinessRuleViolationException;
import com.jashawn.inventory_api.Exceptions.ResourceNotFoundException;
import com.jashawn.inventory_api.department.Department;
import com.jashawn.inventory_api.department.DepartmentRepository;
import com.jashawn.inventory_api.employee.Employee;
import com.jashawn.inventory_api.employee.EmployeeRepository;
import com.jashawn.inventory_api.inventory.dto.*;
import com.jashawn.inventory_api.product.Product;
import com.jashawn.inventory_api.product.ProductRepository;
import com.jashawn.inventory_api.product.dto.ProductDtoMapper;
import com.jashawn.inventory_api.stockItem.StockItem;
import com.jashawn.inventory_api.stockItem.StockItemRepository;
import com.jashawn.inventory_api.stockItem.dto.StockItemDtoMapper;
import com.jashawn.inventory_api.stockItem.dto.StockItemResponse;
import com.jashawn.inventory_api.stockItem.dto.StockItemTransferResponse;
import com.jashawn.inventory_api.stockMovement.StockMovement;
import com.jashawn.inventory_api.stockMovement.StockMovementRepository;
import com.jashawn.inventory_api.warehouse.Warehouse;
import com.jashawn.inventory_api.warehouse.WarehouseRepository;
import com.jashawn.inventory_api.warehouse.dto.WarehouseDtoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final StockItemRepository stockItemRepository;
    private final StockMovementRepository stockMovementRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public InventoryService(StockItemRepository stockItemRepository,
                            StockMovementRepository stockMovementRepository,
                            WarehouseRepository warehouseRepository,
                            ProductRepository productRepository,
                            EmployeeRepository employeeRepository,
                            DepartmentRepository departmentRepository) {
        this.stockItemRepository = stockItemRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Transactional
    public StockItemResponse receive(ReceiveInventoryRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", request.productId().toString()));

        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "ID", request.warehouseId().toString()));

        Employee performedBy = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "ID", request.employeeId().toString()));

        product.enforceActiveState(product.getName());
        warehouse.enforceActiveState(warehouse.getName());
        performedBy.enforceActiveState(performedBy.getFullName());

        StockItem stockItem = stockItemRepository.findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                .orElseGet(() -> StockItem.create(product, warehouse, 0, 0));

        stockItem.receive(request.quantity());
        stockItemRepository.save(stockItem);

        StockMovement stockMovement = StockMovement.receive(stockItem,
                performedBy,
                request.quantity(),
                product.getUnitCost(),
                request.reason(),
                request.reference()
        );

        stockMovementRepository.save(stockMovement);

        return StockItemDtoMapper.toDto(stockItem,
                ProductDtoMapper.toSummaryDto(product),
                WarehouseDtoMapper.toSummaryDto(warehouse)
        );
    }

    @Transactional
    public StockItemResponse issue(IssueStockItemRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", request.productId().toString()));

        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "ID", request.warehouseId().toString()));

        Employee employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee", "ID", request.employeeId().toString())
                );

        Department receivingDepartment = departmentRepository.findById(request.receivingDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department", "ID", request.receivingDepartmentId().toString())
                );

        product.enforceActiveState(product.getName());
        warehouse.enforceActiveState(warehouse.getName());
        receivingDepartment.enforceActiveState(receivingDepartment.getName() + ": Receiving Department");
        employee.enforceActiveState(employee.getFullName());

        StockItem stockItem = stockItemRepository.findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "StockItem", "product id and warehouse id", product.getId() + " " + warehouse.getId())
                );

        stockItem.issue(request.quantity());

        StockMovement stockMovement = StockMovement.issue(
                stockItem,
                employee,
                receivingDepartment,
                product.getUnitCost(),
                request.quantity(),
                request.reason(),
                request.reference()
        );

        stockMovementRepository.save(stockMovement);

        return StockItemDtoMapper.toDto(stockItem,
                ProductDtoMapper.toSummaryDto(product),
                WarehouseDtoMapper.toSummaryDto(warehouse)
        );
    }

    @Transactional
    public StockItemResponse reserve(ReserveStockItemRequest request) {
        Employee employee = employeeRepository.findById(request.performedByEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee", "ID", request.performedByEmployeeId().toString())
                );

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", request.productId().toString()));

        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "ID", request.warehouseId().toString()));

        Department department = departmentRepository.findById(request.reservedForDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department", "ID", request.reservedForDepartmentId().toString())
                );

        employee.enforceActiveState(employee.getFullName());
        product.enforceActiveState(product.getName());
        warehouse.enforceActiveState(warehouse.getName());
        department.enforceActiveState(department.getName());

        StockItem stockItem = stockItemRepository.findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "StockItem", "product id and warehouse id", product.getId() + " " + warehouse.getId())
                );

        stockItem.reserve(request.quantityReserved());

        StockMovement stockMovement = StockMovement.reserve(
                stockItem,
                employee,
                department,
                request.quantityReserved(),
                request.reason(),
                request.reference()
        );

        stockMovementRepository.save(stockMovement);

        return StockItemDtoMapper.toDto(stockItem,
                ProductDtoMapper.toSummaryDto(product),
                WarehouseDtoMapper.toSummaryDto(warehouse)
        );
    }

    @Transactional
    public StockItemResponse releaseReservation(ReleaseReservationRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", request.productId().toString()));

        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "ID", request.warehouseId().toString()));

        Employee employee = employeeRepository.findById(request.performedByEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee", "ID", request.performedByEmployeeId().toString())
                );

        Department department = departmentRepository.findById(request.releasedToDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department", "ID", request.releasedToDepartmentId().toString())
                );

        product.enforceActiveState(product.getName());
        employee.enforceActiveState(employee.getFullName());
        warehouse.enforceActiveState(warehouse.getName());
        department.enforceActiveState(department.getName());

        StockItem stockItem = stockItemRepository.findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "StockItem", "product id and warehouse id", product.getId() + " " + warehouse.getId())
                );

        stockItem.releaseReservation(request.quantityReleased());

        StockMovement stockMovement = StockMovement.releaseReservation(
                stockItem,
                employee,
                department,
                request.quantityReleased(),
                request.reason(),
                request.reference()
        );

        stockMovementRepository.save(stockMovement);

        return StockItemDtoMapper.toDto(
                stockItem,
                ProductDtoMapper.toSummaryDto(product),
                WarehouseDtoMapper.toSummaryDto(warehouse)
        );
    }

    @Transactional
    public StockItemResponse increaseByAdjustment(ManualAdjustmentRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", request.productId().toString()));

        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "ID", request.warehouseId().toString()));

        Department department = null;
        if (request.optionalDepartmentId() != null) {
            department = departmentRepository.findById(request.optionalDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Department", "ID", request.optionalDepartmentId().toString())
                    );
        }

        Employee employee = employeeRepository.findById(request.performedByEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee", "ID", request.performedByEmployeeId().toString())
                );

        product.enforceActiveState(product.getName());
        warehouse.enforceActiveState(warehouse.getName());
        employee.enforceActiveState(employee.getFullName());
        if (department != null) {
            department.enforceActiveState(department.getName());
        }

        StockItem stockItem = stockItemRepository.findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "StockItem", "product id and warehouse id", product.getId() + " " + warehouse.getId())
                );

        stockItem.increaseByAdjustment(request.quantity());

        StockMovement stockMovement = StockMovement.increaseByAdjustment(
                stockItem,
                employee,
                department,
                request.quantity(),
                request.reason(),
                request.reference()
        );

        stockMovementRepository.save(stockMovement);

        return StockItemDtoMapper.toDto(
                stockItem,
                ProductDtoMapper.toSummaryDto(product),
                WarehouseDtoMapper.toSummaryDto(warehouse)
        );
    }

    @Transactional
    public StockItemResponse decreaseByAdjustment(ManualAdjustmentRequest request) {
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", request.productId().toString()));

        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "ID", request.warehouseId().toString()));

        Department department = null;
        if (request.optionalDepartmentId() != null) {
            department = departmentRepository.findById(request.optionalDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Department", "ID", request.optionalDepartmentId().toString())
                    );
        }

        Employee employee = employeeRepository.findById(request.performedByEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee", "ID", request.performedByEmployeeId().toString())
                );

        product.enforceActiveState(product.getName());
        warehouse.enforceActiveState(warehouse.getName());
        employee.enforceActiveState(employee.getFullName());
        if (department != null) {
            department.enforceActiveState(department.getName());
        }

        StockItem stockItem = stockItemRepository.findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "StockItem", "product id and warehouse id", product.getId() + " " + warehouse.getId())
                );

        stockItem.decreaseByAdjustment(request.quantity());

        StockMovement stockMovement = StockMovement.decreaseByAdjustment(
                stockItem,
                employee,
                department,
                request.quantity(),
                request.reason(),
                request.reference()
        );

        stockMovementRepository.save(stockMovement);

        return StockItemDtoMapper.toDto(
                stockItem,
                ProductDtoMapper.toSummaryDto(product),
                WarehouseDtoMapper.toSummaryDto(warehouse)
        );
    }

    @Transactional
    public StockItemTransferResponse transferBetweenWarehouses(WarehouseTransferRequest request) {
        if (request.issuingWarehouseId().equals(request.receivingWarehouseId())) {
            throw new BusinessRuleViolationException(
                    "Warehouse transfer operation", "Transfers within", "single warehouse"
            );
        }

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", request.productId().toString()));

        Employee employee = employeeRepository.findById(request.performedByEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee", "ID", request.performedByEmployeeId().toString())
                );

        Warehouse issuingWarehouse = warehouseRepository.findById(request.issuingWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Issuing warehouse", "ID", request.issuingWarehouseId().toString())
                );

        Warehouse receivingWarehouse = warehouseRepository.findById(request.receivingWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Receiving warehouse", "ID", request.receivingWarehouseId().toString())
                );

        product.enforceActiveState(product.getName());
        employee.enforceActiveState(employee.getFullName());
        issuingWarehouse.enforceActiveState(issuingWarehouse.getName());
        receivingWarehouse.enforceActiveState(receivingWarehouse.getName());

        StockItem issuingStockItem = stockItemRepository
                .findByProductIdAndWarehouseId(product.getId(), issuingWarehouse.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "StockItem", "product id and issuing warehouse id", product.getId() + " " + issuingWarehouse.getId())
                );

        StockItem receivingStockItem = stockItemRepository
                .findByProductIdAndWarehouseId(product.getId(), receivingWarehouse.getId())
                .orElseGet(() -> StockItem.create(product, receivingWarehouse, 0, 0));

        issuingStockItem.issue(request.quantity());
        receivingStockItem.receive(request.quantity());
        stockItemRepository.save(receivingStockItem);

        StockMovement transferOutStockMovement = StockMovement.transferOut(
                issuingStockItem,
                employee,
                request.quantity(),
                request.reason(),
                request.reference()
        );

        StockMovement transferInStockMovement = StockMovement.transferIn(
                receivingStockItem,
                employee,
                request.quantity(),
                request.reason(),
                request.reference()
        );

        stockMovementRepository.save(transferOutStockMovement);
        stockMovementRepository.save(transferInStockMovement);

        return StockItemDtoMapper.toTransferResponse(issuingStockItem,
                ProductDtoMapper.toSummaryDto(product),
                WarehouseDtoMapper.toSummaryDto(issuingWarehouse),
                receivingStockItem,
                WarehouseDtoMapper.toSummaryDto(receivingWarehouse)
        );
    }
}

