package com.jashawn.inventory_api.product;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductSpecifications {

    public static Specification<Product> hasStatus(Boolean status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("isActive"), status);
    }

    public static Specification<Product> hasName(String name) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase().trim() + "%");
    }

    public static Specification<Product> hasSku(UUID sku) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("sku"), sku);
    }

    public static Specification<Product> hasUnitCost(BigDecimal unitCost) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.toBigDecimal(root.get("unitCost")), unitCost);
    }

    public static Specification<Product> isNotDeleted() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("deletedAt"), null);
    }
}
