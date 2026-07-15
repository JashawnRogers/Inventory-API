package com.jashawn.inventory_api.warehouse;

import com.jashawn.inventory_api.Exceptions.InvalidFieldException;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "warehouse")
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "location", nullable = false, unique = true)
    private String location;

    @Column(name = "active")
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected Warehouse() {}

    private Warehouse(String name, String location) {
        this.name = name;
        this.location = location;
    }

    public Warehouse create(String name, String location) {
        if (name == null || name.isBlank()) {
            throw new InvalidFieldException("Warehouse", "name", "null or blank");
        }

        if (location == null || location.isBlank()) {
            throw new InvalidFieldException("Warehouse", "location", "null or blank");
        }

        return new Warehouse(name.trim(), location.trim());
    }

    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidFieldException("Warehouse", "name", "null or blank");
        }

        if (name.trim().equals(this.name)) {
            return;
        }

        this.name = name.trim();
    }

    public void updateLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new InvalidFieldException("Warehouse", "location", "null or blank");
        }

        if (location.trim().equals(this.location)) {
            return;
        }

        this.location = location.trim();
    }

    public void activate() {
        if (this.active) return;

        this.active = true;
    }

    public void deactivate() {
        if (!this.active) return;

        this.active = false;
    }

    @PrePersist
    private void initialize() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
