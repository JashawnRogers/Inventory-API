package com.jashawn.inventory_api.employee.web;

import com.jashawn.inventory_api.employee.dto.EmployeeResponse;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class EmployeeAssembler implements RepresentationModelAssembler<EmployeeResponse, EntityModel<EmployeeResponse>> {

    @Override
    public EntityModel<EmployeeResponse> toModel(EmployeeResponse entity) {
        EntityModel<EmployeeResponse> model = EntityModel.of(entity,
                linkTo(methodOn(EmployeeController.class).findEmployee(entity.id())).withSelfRel()
                );

        if (entity.active()) {
            model.add(linkTo(methodOn(EmployeeController.class).deleteEmployee(entity.id())).withRel("delete"));
        }

        return model;
    }
}
