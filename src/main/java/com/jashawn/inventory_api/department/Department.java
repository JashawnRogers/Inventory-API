package com.jashawn.inventory_api.department;

import com.jashawn.inventory_api.Exceptions.InvalidFieldException;
import com.jashawn.inventory_api.common.ActiveStateEnforcer;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "department")
public class Department implements ActiveStateEnforcer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected Department() {}

    private Department(String name, String code) {
        this.name = name;
        this.code = code;
        this.active = true;
    }

    public static Department create(String name, String code) {
        if (name == null || name.isBlank()) {
            throw new InvalidFieldException("null or blank", "Department", "name");
        }

        if (code == null || code.isBlank()) {
            throw new InvalidFieldException("null or blank", "Department", "code");
        }

        return new Department(name.trim(), code.trim());
    }

    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidFieldException("null or blank", "Department", "name");
        }

        String normalizedName = name.trim();

        if (normalizedName.equals(this.name)) {
            return;
        }

        this.name = normalizedName;
    }

    public void updateCode(String code) {
        if (code == null || code.isBlank()) {
            throw new InvalidFieldException("null or blank", "Department", "code");
        }

        String normalizedCode = code.trim();

        if (normalizedCode.equals(this.code)) {
            return;
        }

        this.code = normalizedCode;
    }

    public void activate() {
        if (this.active) return;

        this.active = true;
    }

    public void deactivate() {
        if (!this.active) return;

        this.active = false;
    }

    public void softDelete() {
        if (this.active) {
            this.active = false;
        }

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
