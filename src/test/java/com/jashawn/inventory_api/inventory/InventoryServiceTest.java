package com.jashawn.inventory_api.inventory;

import com.jashawn.inventory_api.Exceptions.InsufficientAvailableStockException;
import com.jashawn.inventory_api.category.Category;
import com.jashawn.inventory_api.department.Department;
import com.jashawn.inventory_api.department.DepartmentRepository;
import com.jashawn.inventory_api.employee.Employee;
import com.jashawn.inventory_api.employee.EmployeeRepository;
import com.jashawn.inventory_api.inventory.dto.IssueStockItemRequest;
import com.jashawn.inventory_api.product.Product;
import com.jashawn.inventory_api.product.ProductRepository;
import com.jashawn.inventory_api.stockItem.StockItem;
import com.jashawn.inventory_api.stockItem.StockItemRepository;
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
    private StockMovementRepository stockMovementRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

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
    void reserve() {
    }

    @Test
    void releaseReservation() {
    }

    @Test
    void decreaseByAdjustment() {
    }

    @Test
    void transferBetweenWarehouses() {
    }
}