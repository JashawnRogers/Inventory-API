package com.jashawn.inventory_api.department;

import org.springframework.data.jpa.domain.Specification;

public class DepartmentSpecifications {

    public static Specification<Department> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), name);
    }

    public static Specification<Department> hasCode(String code) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("code")), code);
    }

    public static Specification<Department> hasStatus(Boolean status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("active"), status);
    }

    public static Specification<Department> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("deletedAt"), null);
    }
}
