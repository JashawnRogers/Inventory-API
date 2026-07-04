package com.jashawn.inventory_api.supplier;

import com.jashawn.inventory_api.product.Product;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", length = 30, unique = true, nullable = false)
    private String name;

    @Column(name = "email", length = 45)
    private String email;

    @Column(name = "phone", length = 12)
    private String phone;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    // Using cascade types to prevent orphaned products
    @OneToMany(mappedBy = "product", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private List<Product> products;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    private void initializeSupplier() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void setUpdatedAtTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}
