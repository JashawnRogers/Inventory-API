package com.jashawn.inventory_api.stockMovement;

import com.jashawn.inventory_api.Exceptions.InvalidFieldException;
import com.jashawn.inventory_api.Exceptions.InvalidStateException;
import com.jashawn.inventory_api.department.Department;
import com.jashawn.inventory_api.employee.Employee;
import com.jashawn.inventory_api.product.Product;
import com.jashawn.inventory_api.stockItem.MovementType;
import com.jashawn.inventory_api.warehouse.Warehouse;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "stock_movement")
public class StockMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "movement_type", nullable = false)
    private MovementType movementType;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "unit_cost", nullable = false)
    private BigDecimal unitCostAtMovement;

    @Column(name = "total_cost", nullable = false)
    private BigDecimal totalCost;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "reference", nullable = false)
    private String reference;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    protected StockMovement() {}

    private StockMovement(Product product,
                          Warehouse warehouse,
                          Employee employee,
                          Department receivingDepartment,
                          MovementType movementType,
                          int quantity,
                          BigDecimal unitCostAtMovement,
                          BigDecimal totalCost,
                          String reason,
                          String reference
    ) {
        this.product = product;
        this.warehouse = warehouse;
        this.employee = employee;
        this.department = receivingDepartment;
        this.movementType = movementType;
        this.quantity = quantity;
        this.unitCostAtMovement = unitCostAtMovement;
        this.totalCost = totalCost;
        this.reason = reason;
        this.reference = reference;
    }

    public static StockMovement create(
            Product product,
            Warehouse warehouse,
            Employee employee,
            Department receivingDepartment,
            MovementType movementType,
            int quantity,
            BigDecimal unitCostAtMovement,
            String reason,
            String reference
    ) {
        if (product == null) {
            throw new InvalidFieldException("null", "StockMovement", "product");
        }

        if (!product.isActive()) {
            throw new InvalidStateException("StockMovement", product.getName(), "inactive");
        }

        if (warehouse == null) {
            throw new InvalidFieldException("null", "StockMovement", "warehouse");
        }

        if (!warehouse.isActive()) {
            throw new InvalidStateException("StockMovement", warehouse.getName(), "inactive");
        }

        if (employee == null) {
            throw new InvalidFieldException("null", "StockMovement", "employee");
        }

        if (!employee.isActive()) {
            throw new InvalidStateException("StockMovement", employee.getFullName(), "inactive");
        }

        if (movementType == null) {
            throw new InvalidFieldException("null", "StockMovement", "movementType");
        }

        if (quantity < 0) {
            throw new InvalidFieldException(String.valueOf(quantity), "StockMovement", "quantity");
        }

        if (unitCostAtMovement == null) {
            throw new InvalidFieldException("null", "StockMovement", "unitCostAtMovement");
        }

        if (reason == null || reason.isBlank()) {
            throw new InvalidFieldException(String.valueOf(reason), "StockMovement", "reason");
        }

        if (reference == null || reference.isBlank()) {
            throw new InvalidFieldException(String.valueOf(reference), "StockMovement", "reference");
        }

        BigDecimal totalCost = unitCostAtMovement.multiply(BigDecimal.valueOf(quantity));

        return new StockMovement(
                product,
                warehouse,
                employee,
                receivingDepartment,
                movementType,
                quantity,
                unitCostAtMovement,
                totalCost,
                reason.trim(),
                reference.trim()
        );
    }

    @PrePersist
    private void initialize() {
        this.createdAt = LocalDateTime.now();
    }
}
