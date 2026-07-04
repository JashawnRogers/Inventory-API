package com.jashawn.inventory_api.product;

import com.jashawn.inventory_api.category.Category;
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

    @Column(name = "reorder_point", nullable = false)
    @Min(value = 0, message = "The product re-order threshold must be at least 0.")
    private int reorderPoint;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JoinColumn(name = "category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;

    public Product(
            String name,
            String description,
            BigDecimal unitCost,
            int reorderPoint
    ) {
        this.name = name.trim();
        this.description = description.trim();
        this.unitCost = unitCost;
        this.reorderPoint = reorderPoint;
    }

    @PrePersist
    private void generateSku() {
        if (sku == null) {
            this.sku = UUID.randomUUID();
        }
    }

    @PrePersist
    private void setActive() {
        this.isActive = true;
    }

    @PrePersist
    private void setCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
