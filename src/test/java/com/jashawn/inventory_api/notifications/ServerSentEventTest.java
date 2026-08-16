package com.jashawn.inventory_api.notifications;

import com.jashawn.inventory_api.category.Category;
import com.jashawn.inventory_api.category.CategoryRepository;
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
import com.jashawn.inventory_api.supplier.SupplierRepository;
import com.jashawn.inventory_api.warehouse.Warehouse;
import com.jashawn.inventory_api.warehouse.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ServerSentEventTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    private Product product1;
    private Employee employee;
    private Warehouse warehouse1;
    private Department department1;

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
        this.product1 = product1;

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
        this.department1 = department1;

        Employee employee1 = Employee.create("Jashawn", "Builds", "jashawn@builds.com", department1);
        Employee employee2 = Employee.create("Builds", "Jashawn", "builds@jashawn.com", department2);
        employeeRepository.saveAll(List.of(employee1, employee2));
        this.employee = employee1;
    }

    @Test
    void shouldBroadcastLowStockAlertWhenInventoryDrops() throws Exception {
//        Create Server Sent Event HTTP connection pipeline
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/notifications/stream")
                .accept(MediaType.TEXT_EVENT_STREAM))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM))
                .andReturn();

        IssueStockItemRequest payloadRequest = new IssueStockItemRequest(
                product1.getId(),
                warehouse1.getId(),
                employee.getId(),
                department1.getId(),
                6,
                "test",
                "test"
        );

        mockMvc.perform(post("/api/v1/inventory/issue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payloadRequest)))
                .andExpect(status().isOk());

        String streamOutput = mvcResult.getResponse().getContentAsString();

        assert streamOutput.contains("event:LOW_STOCK_ALERT");

        assert streamOutput.contains("\"productId\"");
        assert streamOutput.contains("\"productName\"");
        assert streamOutput.contains("\"availableStock\"");
    }
}
