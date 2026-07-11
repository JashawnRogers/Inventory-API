package com.jashawn.inventory_api.supplier;

import org.springframework.data.jpa.domain.Specification;

public class SupplierSpecifications {

    public static Specification<Supplier> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), name);
    }

    public static Specification<Supplier> hasPhone(String phone) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("phone"), phone);
    }

    public static Specification<Supplier> hasEmail(String email) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(root.get("email"), email);
    }

    public static Specification<Supplier> hasStatus(Boolean status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isActive"), status);
    }

    public static Specification<Supplier> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("deletedAt"), null);
    }
}
