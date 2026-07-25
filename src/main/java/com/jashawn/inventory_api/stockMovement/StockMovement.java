package com.jashawn.inventory_api.stockMovement;

import com.jashawn.inventory_api.Exceptions.InvalidFieldException;
import com.jashawn.inventory_api.department.Department;
import com.jashawn.inventory_api.employee.Employee;
import com.jashawn.inventory_api.stockItem.StockItem;
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
    @JoinColumn(name = "stock_item_id", nullable = false)
    private StockItem stockItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Enumerated(EnumType.STRING)
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

    private StockMovement(StockItem stockItem,
                          Employee employee,
                          Department receivingDepartment,
                          MovementType movementType,
                          int quantity,
                          BigDecimal unitCostAtMovement,
                          BigDecimal totalCost,
                          String reason,
                          String reference
    ) {
        this.stockItem = stockItem;
        this.employee = employee;
        this.department = receivingDepartment;
        this.movementType = movementType;
        this.quantity = quantity;
        this.unitCostAtMovement = unitCostAtMovement;
        this.totalCost = totalCost;
        this.reason = reason;
        this.reference = reference;
    }
    public static StockMovement receive(StockItem stockItem,
                                        Employee employee,
                                        int quantity,
                                        BigDecimal unitCostAtMovement,
                                        String reason,
                                        String reference) {
        if (stockItem == null) {
            throw new InvalidFieldException("null", "StockMovement", "stockItem");
        }

        if (employee == null) {
            throw new InvalidFieldException("null", "StockMovement", "employee");
        }

        if (quantity <= 0) {
            throw new InvalidFieldException(String.valueOf(quantity), "StockMovement", "quantity");
        }

        if (unitCostAtMovement == null) {
            throw new InvalidFieldException("null", "StockMovement", "unitCostAtMovement");
        } else if (unitCostAtMovement.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidFieldException("$" + unitCostAtMovement, "StockMovement", "unitCostAtMovement");
        }

        if (reason == null || reason.isBlank()) {
            throw new InvalidFieldException(reason, "StockMovement", "reason");
        }

        if (reference == null || reference.isBlank()) {
            throw new InvalidFieldException(reference, "StockMovement", "reference");
        }

        BigDecimal totalCost = unitCostAtMovement.multiply(BigDecimal.valueOf(quantity));

        return new StockMovement(
                stockItem,
                employee,
                null,
                MovementType.RECEIVE,
                quantity,
                unitCostAtMovement,
                totalCost,
                reason,
                reference
        );
    }

    public static StockMovement issue(StockItem stockItem,
                               Employee employee,
                               Department receivingDepartment,
                               BigDecimal unitCostAtMovement,
                               int quantity,
                               String reason,
                               String reference) {

        if (stockItem == null) {
            throw new InvalidFieldException("null", "StockMovement", "stockItem");
        }

        if (employee == null) {
            throw new InvalidFieldException("null", "StockMovement", "employee");
        }

        if (receivingDepartment == null) {
            throw new InvalidFieldException("null", "StockMovement", "department");
        }

        if (quantity <= 0) {
            throw new InvalidFieldException(String.valueOf(quantity), "StockMovement", "quantity");
        }

        if (unitCostAtMovement == null) {
            throw new InvalidFieldException("null", "StockMovement", "unitCostAtMovement");
        } else if (unitCostAtMovement.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidFieldException("$" + unitCostAtMovement, "StockMovement", "unitCostAtMovement");
        }

        if (reason == null || reason.isBlank()) {
            throw new InvalidFieldException(reason, "StockMovement", "reason");
        }

        if (reference == null || reference.isBlank()) {
            throw new InvalidFieldException(reference, "StockMovement", "reference");
        }

        BigDecimal totalCost = unitCostAtMovement.multiply(BigDecimal.valueOf(quantity));

        return new StockMovement(
                stockItem,
                employee,
                receivingDepartment,
                MovementType.ISSUE,
                quantity,
                unitCostAtMovement,
                totalCost,
                reason,
                reference
        );
    }

    public static StockMovement reserve(StockItem stockItem,
                                        Employee performedByEmployee,
                                        Department reservedForDepartment,
                                        int quantityReserved,
                                        String reason,
                                        String reference) {
        if (stockItem == null) {
            throw new InvalidFieldException("null", "StockMovement", "stockItem");
        }

        if (performedByEmployee == null) {
            throw new InvalidFieldException("null", "StockMovement", "employee");
        }

        if (reservedForDepartment == null) {
            throw new InvalidFieldException("null", "StockMovement", "department");
        }

        if (quantityReserved <= 0) {
            throw new InvalidFieldException(String.valueOf(quantityReserved), "StockMovement", "quantity");
        }

        if (reason == null || reason.isBlank()) {
            throw new InvalidFieldException(reason, "StockMovement", "reason");
        }

        if (reference == null || reference.isBlank()) {
            throw new InvalidFieldException(reference, "StockMovement", "reference");
        }

        BigDecimal totalCost = stockItem
                .getProduct()
                .getUnitCost()
                .multiply(BigDecimal.valueOf(quantityReserved));

        return new StockMovement(
                stockItem,
                performedByEmployee,
                reservedForDepartment,
                MovementType.RESERVE,
                quantityReserved,
                stockItem.getProduct().getUnitCost(),
                totalCost,
                reason,
                reference
        );
    }

    public static StockMovement releaseReservation(StockItem stockItem,
                                                   Employee performedByEmployee,
                                                   Department releasedToDepartment,
                                                   int quantityReleased,
                                                   String reason,
                                                   String reference) {
        if (stockItem == null) {
            throw new InvalidFieldException("null", "StockMovement", "stockItem");
        }

        if (performedByEmployee == null) {
            throw new InvalidFieldException("null", "StockMovement", "employee");
        }

        if (releasedToDepartment == null) {
            throw new InvalidFieldException("null", "StockMovement", "department");
        }

        if (quantityReleased <= 0) {
            throw new InvalidFieldException(String.valueOf(quantityReleased), "StockMovement", "quantity");
        }

        if (reason == null || reason.isBlank()) {
            throw new InvalidFieldException(reason, "StockMovement", "reason");
        }

        if (reference == null || reference.isBlank()) {
            throw new InvalidFieldException(reference, "StockMovement", "reference");
        }

        BigDecimal unitCost = stockItem.getProduct().getUnitCost();
        BigDecimal totalCost = unitCost.multiply(BigDecimal.valueOf(quantityReleased));

        return new StockMovement(
                stockItem,
                performedByEmployee,
                releasedToDepartment,
                MovementType.RELEASE_RESERVATION,
                quantityReleased,
                unitCost,
                totalCost,
                reason,
                reference
        );
    }

    @PrePersist
    private void initialize() {
        this.createdAt = LocalDateTime.now();
    }
}
