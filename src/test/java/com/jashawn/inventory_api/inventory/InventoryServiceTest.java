package com.jashawn.inventory_api.inventory;

import com.jashawn.inventory_api.Exceptions.InsufficientAvailableStockException;
import com.jashawn.inventory_api.Exceptions.InvalidStockMovementException;
import com.jashawn.inventory_api.category.Category;
import com.jashawn.inventory_api.department.Department;
import com.jashawn.inventory_api.department.DepartmentRepository;
import com.jashawn.inventory_api.employee.Employee;
import com.jashawn.inventory_api.employee.EmployeeRepository;
import com.jashawn.inventory_api.inventory.dto.*;
import com.jashawn.inventory_api.product.Product;
import com.jashawn.inventory_api.product.ProductRepository;
import com.jashawn.inventory_api.stockItem.StockItem;
import com.jashawn.inventory_api.stockItem.StockItemRepository;
import com.jashawn.inventory_api.stockItem.dto.StockItemResponse;
import com.jashawn.inventory_api.stockItem.dto.StockItemTransferResponse;
import com.jashawn.inventory_api.stockMovement.StockMovementRepository;
import com.jashawn.inventory_api.supplier.Supplier;
import com.jashawn.inventory_api.warehouse.Warehouse;
import com.jashawn.inventory_api.warehouse.WarehouseRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private StockItemRepository stockItemRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private StockMovementRepository stockMovementRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    @DisplayName("Cannot issue more stock than what is available")
    void cannotIssueMoreStockThanWhatIsAvailable() throws Exception{
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        UUID receivingDepartmentId = UUID.randomUUID();

        Product product = Product.create(
                "Product",
                "1234",
                "Description",
                BigDecimal.TEN,
                10,
                Category.create("Category1", "Description"),
                Supplier.create("Supplier", "email@email.com", "123-456-7890")
                );

        Warehouse warehouse = Warehouse.create("Warehouse 1", "Phoenix");
        Department receivingDepartment = Department.create("Test", "1122");

        Employee employee = Employee.create(
                "Jashawn",
                "Codes",
                "email@email.com",
                receivingDepartment
        );

        IssueStockItemRequest request = IssueStockItemRequest
                .builder()
                .productId(productId)
                .warehouseId(warehouseId)
                .employeeId(employeeId)
                .receivingDepartmentId(receivingDepartmentId)
                .quantity(50)
                .reason("reason")
                .reference("reference")
                .build();

        StockItem stockItem = StockItem.create(product, warehouse, 50, 30);

        Field[] productFields = product.getClass().getDeclaredFields();
        Field[] warehouseFields = warehouse.getClass().getDeclaredFields();

        for (Field field : productFields) {
            if (field.getName().equals("id")) {
                field.setAccessible(true);
                field.set(product, productId);
            }
        }

        for (Field field : warehouseFields) {
            if (field.getName().equals("id")) {
                field.setAccessible(true);
                field.set(warehouse, warehouseId);
            }
        }

        when(productRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(warehouse));
        when(departmentRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(receivingDepartment));
        when(employeeRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(employee));
        when(stockItemRepository.findByProductIdAndWarehouseId(Mockito.any(UUID.class), Mockito.any(UUID.class)))
                .thenReturn(Optional.of(stockItem));

        assertThrows(InsufficientAvailableStockException.class, () -> inventoryService.issue(request));
    }

    @Test
    @DisplayName("Cannot reserve more stock than what is available")
    void cannotReserveMoreStockThanWhatIsAvailable() throws Exception {
        UUID performedByEmployeeId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        UUID reservedForDepartmentId = UUID.randomUUID();

        Product product = Product.create(
                "Product",
                "1234",
                "description",
                BigDecimal.TEN,
                0,
                Category.create("Category1", "Description"),
                Supplier.create("Supplier", "email@email.com", "123-456-7890")
        );

        Warehouse warehouse = Warehouse.create("Warehouse", "Location");
        Department reservedForDepartment = Department.create("Department", "1234");

        Employee performedByEmployee = Employee.create(
                "Jashawn",
                "Codes",
                "email@email.com",
                reservedForDepartment
                );

        ReserveStockItemRequest request = ReserveStockItemRequest.builder()
                .performedByEmployeeId(performedByEmployeeId)
                .productId(productId)
                .warehouseId(warehouseId)
                .reservedForDepartmentId(reservedForDepartmentId)
                .quantityReserved(100)
                .reason("reason")
                .reference("reference")
                .build();

        StockItem stockItem = StockItem.create(product, warehouse, 50, 0);

        Field[] productFields = product.getClass().getDeclaredFields();
        Field[] warehouseFields = warehouse.getClass().getDeclaredFields();

        for (Field field : productFields) {
            if (field.getName().equals("id")) {
                field.setAccessible(true);
                field.set(product, productId);
            }
        }

        for (Field field : warehouseFields) {
            if (field.getName().equals("id")) {
                field.setAccessible(true);
                field.set(warehouse, warehouseId);
            }
        }

        when(employeeRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(performedByEmployee));
        when(productRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(warehouse));
        when(departmentRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(reservedForDepartment));
        when(stockItemRepository.findByProductIdAndWarehouseId(Mockito.any(UUID.class), Mockito.any(UUID.class)))
                .thenReturn(Optional.of(stockItem));

        assertThrows(InsufficientAvailableStockException.class, () -> inventoryService.reserve(request));
    }

    @Test
    @DisplayName("Cannot release more reserved stock than what is available")
    void cannotReleaseMoreReservedStockThanWhatIsAvailable() throws Exception {
        UUID performedByEmployeeId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        UUID releasedToDepartmentId = UUID.randomUUID();

        Product product = Product.create(
                "Product",
                "1234",
                "description",
                BigDecimal.TEN,
                0,
                Category.create("Category1", "Description"),
                Supplier.create("Supplier", "email@email.com", "123-456-7890")
        );

        Warehouse warehouse = Warehouse.create("Warehouse", "Location");
        Department releasedToDepartment = Department.create("Department", "1234");

        Employee performedByEmployee = Employee.create(
                "Jashawn",
                "Codes",
                "email@email.com",
                releasedToDepartment
        );

        ReleaseReservationRequest request = ReleaseReservationRequest.builder()
                .performedByEmployeeId(performedByEmployeeId)
                .productId(productId)
                .warehouseId(warehouseId)
                .releasedToDepartmentId(releasedToDepartmentId)
                .quantityReleased(100)
                .reason("reason")
                .reference("reference")
                .build();

        StockItem stockItem = StockItem.create(product, warehouse, 50, 0);

        Field[] productFields = product.getClass().getDeclaredFields();
        Field[] warehouseFields = warehouse.getClass().getDeclaredFields();

        for (Field field : productFields) {
            if (field.getName().equals("id")) {
                field.setAccessible(true);
                field.set(product, productId);
            }
        }

        for (Field field : warehouseFields) {
            if (field.getName().equals("id")) {
                field.setAccessible(true);
                field.set(warehouse, warehouseId);
            }
        }

        when(productRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(warehouse));
        when(employeeRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(performedByEmployee));
        when(departmentRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(releasedToDepartment));
        when(stockItemRepository.findByProductIdAndWarehouseId(Mockito.any(UUID.class), Mockito.any(UUID.class)))
                .thenReturn(Optional.of(stockItem));


        assertThrows(InvalidStockMovementException.class, () -> inventoryService.releaseReservation(request));
    }

    @Test
    @DisplayName("Decrease adjustment decreases available stock quantity and does not decrease reserved stock quantity")
    void decreaseByAdjustmentDecreasesAvailableStockQuantityNotReservedQuantity() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID warehouseId = UUID.randomUUID();
        UUID performedByEmployeeId = UUID.randomUUID();

        Product product = Product.create(
                "Product",
                "1234",
                "description",
                BigDecimal.TEN,
                0,
                Category.create("Category1", "Description"),
                Supplier.create("Supplier", "email@email.com", "123-456-7890")
        );

        Warehouse warehouse = Warehouse.create("Warehouse", "Location");
        Department employeeDepartment = Department.create("Department", "1234");

        Employee performedByEmployee = Employee.create(
                "Jashawn",
                "Codes",
                "email@email.com",
                employeeDepartment
        );

        ManualAdjustmentRequest request = ManualAdjustmentRequest.builder()
                .productId(productId)
                .warehouseId(warehouseId)
                .optionalDepartmentId(null)
                .performedByEmployeeId(performedByEmployeeId)
                .quantity(10)
                .reason("reason")
                .reference("reference")
                .build();

        StockItem stockItem = StockItem.create(product, warehouse, 50, 25);

        Field[] productFields = product.getClass().getDeclaredFields();
        Field[] warehouseFields = warehouse.getClass().getDeclaredFields();

        for (Field field : productFields) {
            if (field.getName().equals("id")) {
                field.setAccessible(true);
                field.set(product, productId);
            }
        }

        for (Field field : warehouseFields) {
            if (field.getName().equals("id")) {
                field.setAccessible(true);
                field.set(warehouse, warehouseId);
            }
        }

        when(productRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(product));
        when(warehouseRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(warehouse));
        when(employeeRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(performedByEmployee));
        when(stockItemRepository.findByProductIdAndWarehouseId(Mockito.any(UUID.class), Mockito.any(UUID.class)))
                .thenReturn(Optional.of(stockItem));

        StockItemResponse response = inventoryService.decreaseByAdjustment(request);

        assertEquals(15, response.availableQuantity());
        assertEquals(25, response.reservedQuantity());

    }

    @Test
    @DisplayName("Transfers between warehouses updates both stock items")
    void transferBetweenWarehousesUpdatesBothStockItems() throws Exception {
        UUID productId = UUID.randomUUID();
        UUID issuingWarehouseId = UUID.randomUUID();
        UUID receivingWarehouseId = UUID.randomUUID();
        UUID performedByEmployeeId = UUID.randomUUID();

        Product product = Product.create(
                "Product",
                "1234",
                "description",
                BigDecimal.TEN,
                0,
                Category.create("Category1", "Description"),
                Supplier.create("Supplier", "email@email.com", "123-456-7890")
        );

        Warehouse issuingWarehouse = Warehouse.create("Issuing Warehouse", "Issue");
        Warehouse receivingWarehouse = Warehouse.create("Receiving Warehouse", "Receive");
        Department employeeDepartment = Department.create("Department", "1234");

        Employee performedByEmployee = Employee.create(
                "Jashawn",
                "Codes",
                "email@email.com",
                employeeDepartment
        );

        WarehouseTransferRequest request = WarehouseTransferRequest.builder()
                .productId(productId)
                .issuingWarehouseId(issuingWarehouseId)
                .receivingWarehouseId(receivingWarehouseId)
                .performedByEmployeeId(performedByEmployeeId)
                .quantity(25)
                .reason("reason")
                .reference("reference")
                .build();

        StockItem issuingStockItem = StockItem.create(product, issuingWarehouse, 30, 0);
        StockItem receivingStockItem = StockItem.create(product, receivingWarehouse, 0, 0);

        Field[] productFields = product.getClass().getDeclaredFields();
        Field[] issuingWarehouseFields = issuingWarehouse.getClass().getDeclaredFields();
        Field[] receivingWarehouseFields = receivingWarehouse.getClass().getDeclaredFields();

        for (Field field : productFields) {
            if (field.getName().equals("id")) {
                field.setAccessible(true);
                field.set(product, productId);
            }
        }

        for (Field field : issuingWarehouseFields) {
            if (field.getName().equals("id")) {
                field.setAccessible(true);
                field.set(issuingWarehouse, issuingWarehouseId);
            }
        }

        for (Field field : receivingWarehouseFields) {
            if (field.getName().equals("id")) {
                field.setAccessible(true);
                field.set(receivingWarehouse, receivingWarehouseId);
            }
        }

        when(productRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(product));
        when(employeeRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(performedByEmployee));
        when(warehouseRepository.findById(issuingWarehouseId)).thenReturn(Optional.of(issuingWarehouse));
        when(warehouseRepository.findById(receivingWarehouseId)).thenReturn(Optional.of(receivingWarehouse));
        when(stockItemRepository.findByProductIdAndWarehouseId(productId, issuingWarehouseId))
                .thenReturn(Optional.of(issuingStockItem));
        when(stockItemRepository.findByProductIdAndWarehouseId(productId, receivingWarehouseId))
                .thenReturn(Optional.of(receivingStockItem));

        StockItemTransferResponse response = inventoryService.transferBetweenWarehouses(request);

        assertEquals(5, response.issuingAvailableQuantity());
        assertEquals(25, response.receivingAvailableQuantity());
    }
}