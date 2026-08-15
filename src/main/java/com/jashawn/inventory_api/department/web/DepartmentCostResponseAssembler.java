package com.jashawn.inventory_api.department.web;

import com.jashawn.inventory_api.reports.dto.DepartmentCostResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class DepartmentCostResponseAssembler
implements RepresentationModelAssembler<DepartmentCostResponse, EntityModel<DepartmentCostResponse>> {
    @Override
    public EntityModel<DepartmentCostResponse> toModel(@NonNull DepartmentCostResponse entity) {
        return EntityModel.of(entity,
                linkTo(methodOn(DepartmentController.class).findDepartment(entity.id())).withSelfRel());
    }
}
