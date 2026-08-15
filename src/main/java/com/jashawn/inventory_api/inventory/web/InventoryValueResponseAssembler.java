package com.jashawn.inventory_api.inventory.web;

import com.jashawn.inventory_api.inventory.dto.InventoryValueResponse;
import com.jashawn.inventory_api.product.web.ProductController;
import org.jspecify.annotations.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class InventoryValueResponseAssembler
implements RepresentationModelAssembler<InventoryValueResponse, EntityModel<InventoryValueResponse>> {
    @Override
    public EntityModel<InventoryValueResponse> toModel(@NonNull InventoryValueResponse entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(ProductController.class).findProduct(entity.productId())).withRel("product"));
    }
}
