package com.jashawn.inventory_api.department.web;

import com.jashawn.inventory_api.department.dto.DepartmentResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class DepartmentAssembler implements RepresentationModelAssembler<DepartmentResponse, EntityModel<DepartmentResponse>> {

    @Override
    public EntityModel<DepartmentResponse> toModel(@NonNull DepartmentResponse entity) {
        EntityModel<DepartmentResponse> model = EntityModel.of(entity,
                linkTo(methodOn(DepartmentController.class).findDepartment(entity.id())).withSelfRel()
        );

        if (entity.active()) {
            model.add(linkTo(methodOn(DepartmentController.class).deleteDepartment(entity.id())).withRel("delete"));
        }

        return model;
    }
}
