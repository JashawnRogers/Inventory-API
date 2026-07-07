package com.jashawn.inventory_api.product.dto;

import com.jashawn.inventory_api.category.dto.CategoryResponse;
import com.jashawn.inventory_api.product.Product;
import com.jashawn.inventory_api.supplier.dto.SupplierResponse;

public class ProductDtoMapper {


    public static ProductResponse toDto(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getUnitCost(),
                product.getReorderPoint(),
                product.isActive(),
                product.getCategory().getId(),
                product.getSupplier().getId(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
