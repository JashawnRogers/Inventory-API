package com.jashawn.inventory_api.category;

import org.springframework.data.jpa.domain.Specification;

public class CategorySpecifications {

    public static Specification<Category> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), name);
    }

    public static Specification<Category> hasStatus(Boolean status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isActive"), status);
    }

    public static Specification<Category> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("deletedAt"), null);
    }
}
