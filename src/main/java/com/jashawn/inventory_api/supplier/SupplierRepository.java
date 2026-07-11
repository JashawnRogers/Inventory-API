package com.jashawn.inventory_api.supplier;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SupplierRepository extends JpaRepository<Supplier, UUID>, JpaSpecificationExecutor<Supplier> {
}
