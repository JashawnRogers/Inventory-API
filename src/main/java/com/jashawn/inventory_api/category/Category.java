package com.jashawn.inventory_api.category;

import com.jashawn.inventory_api.Exceptions.DuplicateResourceException;
import com.jashawn.inventory_api.Exceptions.InvalidFieldException;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", length = 25, nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected Category() {}

    private Category(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static Category create(String name, String description) {
        if (name == null || !name.isBlank()) {
            throw new InvalidFieldException(name, "Category", "name");
        }

        if (description != null) {
            return new Category(name.trim(), description.trim());
        }

        return new Category(name.trim(), null);
    }

    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidFieldException(name, "Category", "name");
        }

        if (this.name.equals(name)) {
            throw new DuplicateResourceException("Category", "name", name);
        }

        this.name = name.trim();
    }

    public void updateDescription(String description) {
        if (description == null || description.isBlank()) {
            this.description = description;
        } else {
            this.description = description.trim();
        }
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void softDelete() {
        this.isActive = false;
        this.deletedAt = LocalDateTime.now();
    }

    @PrePersist
    private void initialize() {
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
