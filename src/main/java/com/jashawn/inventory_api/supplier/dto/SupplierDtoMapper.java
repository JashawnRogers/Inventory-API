package com.jashawn.inventory_api.supplier.dto;

import com.jashawn.inventory_api.product.dto.ProductResponse;
import com.jashawn.inventory_api.supplier.Supplier;

import java.util.List;

public class SupplierDtoMapper {

    public static SupplierResponse toDto(Supplier supplier) {
        return new SupplierResponse(
                supplier.getId(),
                supplier.getName(),
                supplier.getEmail(),
                supplier.getPhone(),
                supplier.isActive(),
                supplier.getCreatedAt(),
                supplier.getUpdatedAt()
        );
    }
}
