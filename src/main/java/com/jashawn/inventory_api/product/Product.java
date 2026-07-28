package com.jashawn.inventory_api.product;

import com.jashawn.inventory_api.Exceptions.*;
import com.jashawn.inventory_api.category.Category;
import com.jashawn.inventory_api.common.ActiveStateEnforcer;
import com.jashawn.inventory_api.supplier.Supplier;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "products")
public class Product implements ActiveStateEnforcer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

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

    @Column(name = "active", nullable = false)
    private boolean active;

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
            String sku,
            String description,
            BigDecimal unitCost,
            Integer reorderPoint,
            Category category,
            Supplier supplier
    ) {
        if (name == null || name.isBlank()) {
            throw new InvalidFieldException(name, "Product", "name");
        } else {
            this.name = name.trim();
        }

        if (sku == null || sku.isBlank()) {
            throw new InvalidFieldException(sku, "Product", "sku");
        } else {
            this.sku = sku.trim();
        }

        if (description != null) {
            this.description = description.trim();
        }

        if (unitCost.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidFieldException("$" + unitCost, "Product", "unitCost");
        } else {
            this.unitCost = unitCost;
        }

        if (reorderPoint < 0) {
            throw new InvalidFieldException(reorderPoint.toString(), "Product", "reorderPoint");
        } else {
            this.reorderPoint = reorderPoint;
        }

        if (category == null) {
            throw new InvalidFieldException("null", "Product", "category");
        } else {
            this.category = category;
        }

        if (supplier == null) {
            throw new InvalidFieldException("null", "Product", "supplier");
        } else {
            this.supplier = supplier;
        }

        this.active = true;
    }

    public static Product create(String name,
                             String sku,
                             String description,
                             BigDecimal unitCost,
                             Integer reorderPoint,
                             Category category,
                             Supplier supplier
    ) {
        return new Product(
                name,
                sku,
                description,
                unitCost,
                reorderPoint,
                category,
                supplier
        );
    }

    public boolean canBeUpdated() {
        return !this.active && this.deletedAt != null;
    }

    public void updateReorderPoint(int value) {
        if (canBeUpdated()) {
            throw new InvalidFieldException(String.valueOf(value), "Product", "reorderPoint");
        }

        if (value < 0) {
            throw new InvalidFieldException(String.valueOf(value), "Product", "reorderPoint");
        }

        this.reorderPoint = value;
    }

    public void updateUnitCost(BigDecimal unitCost) {
        if (unitCost != null && unitCost.compareTo(BigDecimal.ZERO) > 0) {
            throw new InvalidFieldException("$" + unitCost, "Product", "unitCost");
        }

        this.unitCost = unitCost;
    }

    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidFieldException(name, "Product", "name");
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

    public void updateCategory(Category category) {
        if (canBeUpdated()) {
            throw new InvalidStateException("Product", category.getName(), "DELETED");
        }

        if (!category.isActive()) {
            throw new InvalidStateException("Product", category.getName(), "inactive");
        }

        if (category.getId().equals(this.category.getId())) {
            return;
        }

        this.category = category;
    }

    public void updateSupplier(Supplier supplier) {
        if (canBeUpdated()) {
            throw new InvalidStateException("Product", supplier.getName(), "DELETED");
        }

        if (!supplier.isActive()) {
            throw new InactiveResourceException(this.name, "Supplier: " + supplier.getName());
        }

        if (supplier.getId().equals(this.supplier.getId())) {
            return;
        }

        this.supplier = supplier;
    }

    public void softDelete() {
        this.active = false;
        this.deletedAt = LocalDateTime.now();
    }

    @PrePersist
    private void initialize() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
