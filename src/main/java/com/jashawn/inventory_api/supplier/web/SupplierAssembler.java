package com.jashawn.inventory_api.supplier.web;

import com.jashawn.inventory_api.supplier.dto.SupplierResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class SupplierAssembler implements RepresentationModelAssembler<SupplierResponse, EntityModel<SupplierResponse>> {

    @Override
    public EntityModel<SupplierResponse> toModel(@NonNull SupplierResponse entity) {
        EntityModel<SupplierResponse> model = EntityModel.of(entity,
                    linkTo(methodOn(SupplierController.class).findSupplier(entity.id())).withSelfRel()
                );

        if (entity.isActive()) {
            model.add(linkTo(methodOn(SupplierController.class).softDelete(entity.id())).withRel("delete"));
        }

        return model;
    }
}
