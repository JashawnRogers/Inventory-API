package com.jashawn.inventory_api.supplier.dto;

public record UpdateSupplierRequest(String name, String email, String phone, Boolean status) {
}
