package com.jashawn.inventory_api.inventory.web;

import com.jashawn.inventory_api.inventory.dto.InventoryValueByWarehouseResponse;
import com.jashawn.inventory_api.product.web.ProductController;
import com.jashawn.inventory_api.warehouse.web.WarehouseController;
import org.jspecify.annotations.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class InventoryValueByWarehouseAssembler
implements RepresentationModelAssembler
        <InventoryValueByWarehouseResponse, EntityModel<InventoryValueByWarehouseResponse>> {
    @Override
    public EntityModel<InventoryValueByWarehouseResponse> toModel(@NonNull InventoryValueByWarehouseResponse entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(WarehouseController.class).findWarehouse(entity.warehouseId())).withRel("warehouse"))
                .add(linkTo(methodOn(ProductController.class).findProduct(entity.productId())).withRel("product"));
    }
}
