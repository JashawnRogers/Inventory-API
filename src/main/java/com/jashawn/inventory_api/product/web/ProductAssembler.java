package com.jashawn.inventory_api.product.web;

import com.jashawn.inventory_api.product.dto.ProductResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;


@Component
public class ProductAssembler implements RepresentationModelAssembler<ProductResponse, EntityModel<ProductResponse>> {

    @Override
    public EntityModel<ProductResponse> toModel(@NonNull ProductResponse entity) {
        EntityModel<ProductResponse> model = EntityModel.of(entity,
                linkTo(methodOn(ProductController.class).findProduct(entity.id())).withSelfRel()
                );

        if (entity.isActive()) {
            model.add(linkTo(methodOn(ProductController.class).softDeleteProduct(entity.id())).withRel("delete"));
        }

        return model;
    }
}
