package com.jashawn.inventory_api.supplier;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
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
