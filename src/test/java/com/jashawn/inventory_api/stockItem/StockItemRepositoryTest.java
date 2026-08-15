package com.jashawn.inventory_api.stockItem;

import com.jashawn.inventory_api.category.Category;
import com.jashawn.inventory_api.category.CategoryRepository;
import com.jashawn.inventory_api.department.Department;
import com.jashawn.inventory_api.department.DepartmentRepository;
import com.jashawn.inventory_api.employee.Employee;
import com.jashawn.inventory_api.employee.EmployeeRepository;
import com.jashawn.inventory_api.inventory.projection.InventoryValue;
import com.jashawn.inventory_api.inventory.projection.InventoryValueByWarehouse;
import com.jashawn.inventory_api.product.Product;
import com.jashawn.inventory_api.product.ProductRepository;
import com.jashawn.inventory_api.stockItem.dto.StockAvailability;
import com.jashawn.inventory_api.stockItem.dto.StockAvailabilityByWarehouse;
import com.jashawn.inventory_api.stockMovement.StockMovement;
import com.jashawn.inventory_api.stockMovement.StockMovementRepository;
import com.jashawn.inventory_api.supplier.Supplier;
import com.jashawn.inventory_api.supplier.SupplierRepository;
import com.jashawn.inventory_api.warehouse.Warehouse;
import com.jashawn.inventory_api.warehouse.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StockItemRepositoryTest {

    @Autowired
    StockMovementRepository stockMovementRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    WarehouseRepository warehouseRepository;

    @Autowired
    StockItemRepository stockItemRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    TestEntityManager testEntityManager;

    private Warehouse warehouse1;
    private Product product1;

    private final LocalDateTime startDate = LocalDateTime.now().minusYears(3);
    private final LocalDateTime endDate = LocalDateTime.now().plusYears(3);

    @BeforeEach
    void setupSharedData() {

        stockMovementRepository.deleteAll();
        stockItemRepository.deleteAll();
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();
        productRepository.deleteAll();
        warehouseRepository.deleteAll();
        supplierRepository.deleteAll();
        categoryRepository.deleteAll();

        Category category1 = Category.create("Category 1", "description");
        Category category2 = Category.create("Category 2", "description");
        categoryRepository.saveAll(List.of(category1, category2));

        Supplier supplier1 = Supplier.create("Supplier 1", "supplier1@supplier1.com", "1234567890");
        Supplier supplier2 = Supplier.create("Supplier 2", "supplier2@supplier2.com", "2234567890");
        supplierRepository.saveAll(List.of(supplier1, supplier2));

        Product product1 = Product.create("Product 1", "123", "description", BigDecimal.TEN, 5, category1, supplier1);
        Product product2 = Product.create("Product 2", "124", "description", BigDecimal.valueOf(25), 10, category2, supplier2);
        this.product1 = productRepository.save(product1);
        productRepository.save(product2);

        Warehouse warehouse1 = Warehouse.create("Warehouse 1", "Phoenix");
        Warehouse warehouse2 = Warehouse.create("Warehouse 2", "Tempe");
        warehouseRepository.saveAll(List.of(warehouse1,warehouse2));
        this.warehouse1 = warehouse1;

        StockItem stockItem1 = StockItem.create(product1, warehouse1, 10, 0);
        StockItem stockItem2 = StockItem.create(product2, warehouse2, 15, 0);
        stockItemRepository.saveAll(List.of(stockItem2, stockItem1));

        Department department1 = Department.create("Department 1", "1111");
        Department department2 = Department.create("Department 2", "2222");
        departmentRepository.saveAll(List.of(department1, department2));

        Employee employee1 = Employee.create("Jashawn", "Builds", "jashawn@builds.com", department1);
        Employee employee2 = Employee.create("Builds", "Jashawn", "builds@jashawn.com", department2);
        employeeRepository.saveAll(List.of(employee1, employee2));

        stockItem1.issue(5);
        stockItemRepository.save(stockItem1);

        StockMovement stockMovement1 = StockMovement.issue(stockItem1, employee1, department1, stockItem1.getProduct().getUnitCost(), 5, "because", "2222");
        StockMovement stockMovement2 = StockMovement.issue(stockItem1, employee2, department2, stockItem2.getProduct().getUnitCost(), 10, "because twice", "1111");
        stockMovementRepository.saveAll(List.of(stockMovement1, stockMovement2));
    }

    @Test
    void getStockItemsBetweenDateRange() {
        Page<StockItem> result = stockItemRepository.getStockItemsBetweenDateRange(
                startDate,
                endDate,
                PageRequest.of(0, 25)
        );

        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());

        assertTrue(result.getContent().stream()
                .allMatch(si -> !si.getCreatedAt().isBefore(startDate) && !si.getCreatedAt().isAfter(endDate)));
    }

    @Test
    void getLowStockReport() {
        Page<StockAvailability> result = stockItemRepository.getLowStockReport(PageRequest.of(0, 25));

        assertEquals(1, result.getTotalElements());

        StockAvailability stockAvailability = result.getContent()
                .stream()
                .findFirst()
                .orElseThrow();

        assertEquals(5, stockAvailability.getAvailableStock());
    }

    @Test
    void getLowStockReportForProductAndWarehouse() {
        StockAvailabilityByWarehouse result = stockItemRepository.getLowStockReportForProductAndWarehouse(
                product1.getId(),
                warehouse1.getId()
        ).orElseThrow();

        assertEquals(5, result.getAvailableStock());
        assertEquals("Warehouse 1", result.getWarehouseName());
    }

    @Test
    void getInventoryValueForProductAndWarehouse() {
        InventoryValueByWarehouse result = stockItemRepository.getInventoryValueForProductAndWarehouse(
                product1.getId(),
                warehouse1.getId()
        );

        assertEquals(BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_UP), result.getInventoryValue());
    }

    @Test
    void getGlobalInventoryValue() {
        Page<InventoryValue> result = stockItemRepository.getGlobalInventoryValue(PageRequest.of(0, 25));

        InventoryValue firstProduct = result.getContent()
                .stream()
                .filter(inventoryValue -> !inventoryValue.getProductId().equals(product1.getId()))
                .findFirst()
                .orElseThrow();

        assertEquals(BigDecimal.valueOf(375).setScale(2, RoundingMode.HALF_UP), firstProduct.getInventoryValue());
    }
}