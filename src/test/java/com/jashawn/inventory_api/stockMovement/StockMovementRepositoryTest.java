package com.jashawn.inventory_api.stockMovement;

import com.jashawn.inventory_api.category.Category;
import com.jashawn.inventory_api.category.CategoryRepository;
import com.jashawn.inventory_api.department.Department;
import com.jashawn.inventory_api.department.DepartmentRepository;
import com.jashawn.inventory_api.employee.Employee;
import com.jashawn.inventory_api.employee.EmployeeRepository;
import com.jashawn.inventory_api.product.Product;
import com.jashawn.inventory_api.product.ProductRepository;
import com.jashawn.inventory_api.reports.ReportService;
import com.jashawn.inventory_api.reports.dto.MovementHistoryRequest;
import com.jashawn.inventory_api.stockItem.StockItem;
import com.jashawn.inventory_api.stockItem.StockItemRepository;
import com.jashawn.inventory_api.stockMovement.dto.DepartmentCostReport;
import com.jashawn.inventory_api.stockMovement.dto.StockMovementResponse;
import com.jashawn.inventory_api.supplier.Supplier;
import com.jashawn.inventory_api.supplier.SupplierRepository;
import com.jashawn.inventory_api.warehouse.Warehouse;
import com.jashawn.inventory_api.warehouse.WarehouseRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
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
@ExtendWith(MockitoExtension.class)
class StockMovementRepositoryTest {

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

    private Department department2;

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
        productRepository.saveAll(List.of(product1, product2));

        Warehouse warehouse1 = Warehouse.create("Warehouse 1", "Phoenix");
        Warehouse warehouse2 = Warehouse.create("Warehouse 2", "Tempe");
        warehouseRepository.saveAll(List.of(warehouse1,warehouse2));

        StockItem stockItem1 = StockItem.create(product1, warehouse1, 10, 0);
        StockItem stockItem2 = StockItem.create(product2, warehouse2, 15, 0);
        stockItemRepository.saveAll(List.of(stockItem2, stockItem1));

        Department department1 = Department.create("Department 1", "1111");
        Department department2 = Department.create("Department 2", "2222");
        this.department2 = departmentRepository.save(department2);
        departmentRepository.save(department1);

        Employee employee1 = Employee.create("Jashawn", "Builds", "jashawn@builds.com", department1);
        Employee employee2 = Employee.create("Builds", "Jashawn", "builds@jashawn.com", department2);
        employeeRepository.saveAll(List.of(employee1, employee2));

        StockMovement stockMovement1 = StockMovement.issue(stockItem1, employee1, department1, stockItem1.getProduct().getUnitCost(), 5, "because", "2222");
        StockMovement stockMovement2 = StockMovement.issue(stockItem1, employee2, department2, stockItem2.getProduct().getUnitCost(), 5, "because twice", "1111");
        stockMovementRepository.saveAll(List.of(stockMovement1, stockMovement2));
    }

    @Test
    void movementHistoryWithinDateRange() {

      Page<StockMovement> result = stockMovementRepository.movementHistoryWithinDateRange(
              startDate,
              endDate,
              PageRequest.of(0, 25)
      );

      assertEquals(2, result.getTotalElements());
      assertEquals(2, result.getContent().size());

      assertTrue(result.getContent().stream()
              .allMatch(sm -> !sm.getCreatedAt().isBefore(startDate) && !sm.getCreatedAt().isAfter(endDate)));

    }

    @Test
    void findByDepartment() {

        Page<StockMovement> result = stockMovementRepository.findByDepartment(
                    department2.getId(),
                    startDate,
                    endDate,
                    PageRequest.of(0, 25)
                );

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        assertTrue(result.getContent().stream()
                .allMatch(sm -> !sm.getCreatedAt().isBefore(startDate) && !sm.getCreatedAt().isAfter(endDate))
        );
    }

    @Test
    void getDepartmentCostsByDateRange() {

        Page<DepartmentCostReport> result = stockMovementRepository.getDepartmentCostsByDateRange(
                startDate,
                endDate,
                PageRequest.of(0, 25)
        );

      assertEquals(2, result.getTotalElements());

      DepartmentCostReport department2Report = result.getContent()
              .stream()
              .filter(report -> report.getDepartmentId().equals(department2.getId()))
              .findFirst()
              .orElseThrow();

      assertEquals(BigDecimal.valueOf(125).setScale(2, RoundingMode.HALF_UP), department2Report.getTotalCost());
      assertEquals("Department 2", department2Report.getDepartmentName());
      assertEquals("2222", department2Report.getDepartmentCode());
    }
}