package com.jashawn.inventory_api.inventory.web;

import com.jashawn.inventory_api.product.web.ProductController;
import com.jashawn.inventory_api.stockItem.dto.StockItemTransferResponse;
import com.jashawn.inventory_api.warehouse.web.WarehouseController;
import org.jspecify.annotations.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class StockItemTransferResponseAssembler
        implements RepresentationModelAssembler<StockItemTransferResponse, EntityModel<StockItemTransferResponse>> {

    @Override
    public EntityModel<StockItemTransferResponse> toModel(@NonNull StockItemTransferResponse entity) {
        return EntityModel.of(entity)
                .add(linkTo(methodOn(ProductController.class).findProduct(entity.product().id())).withRel("product"))
                .add(linkTo(methodOn(WarehouseController.class)
                        .findWarehouse(entity.issuingWarehouse().id())).withRel("issuing_warehouse"))
                .add(linkTo(methodOn(WarehouseController.class)
                        .findWarehouse(entity.receivingWarehouse().id())).withRel("receiving_warehouse"));
    }
}
