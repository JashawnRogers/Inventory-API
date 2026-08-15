package com.jashawn.inventory_api.warehouse.web;

import com.jashawn.inventory_api.warehouse.dto.WarehouseResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class WarehouseAssembler implements RepresentationModelAssembler<WarehouseResponse, EntityModel<WarehouseResponse>> {
    @Override
    public EntityModel<WarehouseResponse> toModel(@NonNull WarehouseResponse entity) {
        EntityModel<WarehouseResponse> model = EntityModel.of(entity,
                linkTo(methodOn(WarehouseController.class).findWarehouse(entity.id())).withSelfRel()
                );

        if (entity.active()) {
            model.add(linkTo(methodOn(WarehouseController.class).deleteWarehouse(entity.id())).withRel("delete"));
        }

        return model;
    }
}
