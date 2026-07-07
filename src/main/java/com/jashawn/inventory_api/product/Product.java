package com.jashawn.inventory_api.product;

import com.jashawn.inventory_api.Exceptions.BusinessRuleViolationException;
import com.jashawn.inventory_api.Exceptions.InvalidStateException;
import com.jashawn.inventory_api.category.Category;
import com.jashawn.inventory_api.supplier.Supplier;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "sku", nullable = false, unique = true)
    private UUID sku;

    @Column(name = "name", length = 25, nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "unit_cost", nullable = false, precision = 10, scale = 2)
    @DecimalMin(value = "0.00", message = "Unit cost must be at least $0.00.")
    private BigDecimal unitCost;

    @Column(name = "reorder_point")
    @Min(value = 0, message = "The product re-order threshold must be at least 0.")
    private Integer reorderPoint;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @JoinColumn(name = "category_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    @JoinColumn(name = "supplier_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Supplier supplier;

    protected Product() {}

    private Product(
            String name,
            String description,
            BigDecimal unitCost,
            Integer reorderPoint,
            Category category,
            Supplier supplier
    ) {
        if (name == null || !name.isBlank()) {
            throw new InvalidStateException("Missing name. Unable to create a product without a name.");
        } else {
            this.name = name.trim();
        }

        if (description != null) {
            this.description = description.trim();
        }

        if (unitCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidStateException("Unit cost must be at least $0.00.");
        } else {
            this.unitCost = unitCost;
        }

        if (reorderPoint < 0) {
            throw new InvalidStateException("Re-order threshold cannot be negative.");
        } else {
            this.reorderPoint = reorderPoint;
        }

        if (category == null) {
            throw new InvalidStateException("Missing category. Unable to create a product without a category.");
        } else {
            this.category = category;
        }

        if (supplier == null) {
            throw new InvalidStateException("Missing supplier. Unable to create a product without a supplier.");
        } else {
            this.supplier = supplier;
        }
    }

    public static Product of(String name,
                             String description,
                             BigDecimal unitCost,
                             Integer reorderPoint,
                             Category category,
                             Supplier supplier
    ) {
        return new Product(
                name,
                description,
                unitCost,
                reorderPoint,
                category,
                supplier
        );
    }

    public boolean canBeUpdated() {
        return this.isActive || this.deletedAt == null;
    }

    public void updateReorderPoint(int reorderPoint) {
        if (!canBeUpdated()) {
            throw new BusinessRuleViolationException("Product is inactive. Cannot perform update.");
        }

        if (reorderPoint < 0) {
            throw new InvalidStateException("Re-order threshold cannot be negative.");
        }

        this.reorderPoint = reorderPoint;
    }

    public void updateUnitCost(BigDecimal unitCost) {
        if (unitCost != null && unitCost.compareTo(BigDecimal.ZERO) > 0) {
            throw new InvalidStateException("Unit cost must be at least $0.00");
        }

        this.unitCost = unitCost;
    }

    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidStateException("Missing product name.");
        }

        this.name = name;
    }

    public void updateDescription(String description) {
        if (description == null || description.isBlank()) {
            this.description = description;
        } else {
            this.description = description.trim();
        }
    }

    public void softDelete() {
        this.isActive = false;
        this.deletedAt = LocalDateTime.now();
    }

    @PrePersist
    private void initialize() {
        if (sku == null) {
            this.sku = UUID.randomUUID();
        }

        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
