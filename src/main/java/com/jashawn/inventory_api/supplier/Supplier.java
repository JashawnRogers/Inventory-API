package com.jashawn.inventory_api.supplier;

import com.jashawn.inventory_api.Exceptions.InvalidFieldException;
import com.jashawn.inventory_api.util.ValidationUtils;
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

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected Supplier() {}

    private Supplier(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public static Supplier create(String name, String email, String phone) {
        if (name == null || name.isBlank()) {
            throw new InvalidFieldException(name, "Supplier", "name");
        }

        if (phone != null && !ValidationUtils.isValidUSPhone(phone)) {
            throw new InvalidFieldException(phone, "Supplier", "phone");
        } else if (phone == null) {
            throw new InvalidFieldException("null", "Supplier", "phone");
        }

        if (email != null && !ValidationUtils.isValidEmail(email)) {
            throw new InvalidFieldException(email, "Supplier", "email");
        } else if (email == null) {
            throw new InvalidFieldException("null", "Supplier", "email");
        }

        String formattedPhone = ValidationUtils.formatPhone(phone);

        return new Supplier(name.trim(), email.trim(), formattedPhone.trim());
    }

    public void updateName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidFieldException(name, "Supplier", "name");
        }

        this.name = name.trim();
    }

    public void updateEmail(String email) {
        if (!ValidationUtils.isValidEmail(email)) {
            throw new InvalidFieldException(email, "Supplier", "email");
        }

        this.email = email;
    }

    public void updatePhone(String phone) {
        if (!ValidationUtils.isValidUSPhone(phone)) {
            throw new InvalidFieldException(phone, "Supplier", "phone");
        }

        this.email = ValidationUtils.formatPhone(phone).trim();
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }

    public void softDelete() {
        this.isActive = false;
        this.deletedAt = LocalDateTime.now();
    }

    @PrePersist
    private void initializeSupplier() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void setUpdatedAtTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}
