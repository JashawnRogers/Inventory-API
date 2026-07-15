package com.jashawn.inventory_api.employee;

import com.jashawn.inventory_api.Exceptions.InvalidFieldException;
import com.jashawn.inventory_api.Exceptions.InvalidStateException;
import com.jashawn.inventory_api.department.Department;
import com.jashawn.inventory_api.util.ValidationUtils;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email")
    private String email;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "active")
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    protected Employee() {}

    private Employee(String firstName, String lastName, String email, Department department) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.department = department;
    }

    public static Employee create(String firstName, String lastName, String email, Department department) {
        if (firstName == null || firstName.isBlank()) {
            throw new InvalidFieldException("null or blank", "Employee", "firstName");
        }

        if (lastName == null || lastName.isBlank()) {
            throw new InvalidFieldException("null or blank", "Employee", "lastName");
        }

        if (!ValidationUtils.isValidEmail(email)) {
            throw new InvalidFieldException("invalid", "Employee", "email");
        }

        if (!department.isActive()) {
            throw new InvalidStateException("Employee", "department", "inactive");
        }

        return new Employee(firstName.trim(), lastName.trim(), email, department);
    }

    public void updateFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            throw new InvalidFieldException("null or blank", "Employee", "firstName");
        }

        String normalizedFirstName = firstName.trim();

        if (normalizedFirstName.equals(this.firstName)) {
            return;
        }

        this.firstName = normalizedFirstName;
    }

    public void updateLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new InvalidFieldException("null or blank", "Employee", "lastName");
        }

        String normalizedLastName = lastName.trim();

        if (normalizedLastName.equals(this.lastName)) {
            return;
        }

        this.lastName = normalizedLastName;
    }

    public void updateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidFieldException("null or blank", "Employee", "email");
        }

        String normalizedEmail = email.trim();

        if (!ValidationUtils.isValidEmail(normalizedEmail)) {
            throw new InvalidFieldException("invalid", "Employee", "email");
        }

        // My attempt at preventing an NPE since an email is not required
        if (this.email != null) {
            if (normalizedEmail.equals(this.email)) {
                return;
            }
        }

        this.email = normalizedEmail;
    }

    public void updatedDepartment(Department department) {
        if (department == null) {
            throw new InvalidFieldException("null", "Employee", "department");
        }

        if (!department.isActive()) {
            throw new InvalidStateException("Employee", "department", "inactive");
        }

        this.department = department;
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
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    private void setUpdatedAt() {
        this.updatedAt = LocalDateTime.now();
    }
}
