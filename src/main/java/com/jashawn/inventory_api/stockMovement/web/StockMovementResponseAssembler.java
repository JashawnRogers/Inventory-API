package com.jashawn.inventory_api.stockMovement.web;

import com.jashawn.inventory_api.department.web.DepartmentController;
import com.jashawn.inventory_api.employee.web.EmployeeController;
import com.jashawn.inventory_api.product.web.ProductController;
import com.jashawn.inventory_api.stockMovement.dto.StockMovementResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class StockMovementResponseAssembler
implements RepresentationModelAssembler<StockMovementResponse, EntityModel<StockMovementResponse>> {
    @Override
    public EntityModel<StockMovementResponse> toModel(@NonNull StockMovementResponse entity) {
        EntityModel<StockMovementResponse> model = EntityModel.of(entity)
                .add(linkTo(methodOn(StockMovementController.class).findStockMovementById(entity.id())).withSelfRel())
                .add(linkTo(methodOn(ProductController.class).findProduct(entity.product().id())).withRel("product"))
                .add(linkTo(methodOn(EmployeeController.class).findEmployee(entity.employee().id())).withRel("employee"));

        if (entity.movedToDepartment() != null) {
            model.add(linkTo(methodOn(DepartmentController.class).findDepartment(entity.movedToDepartment().id()))
                    .withRel("movedToDepartment"));
        }

        return model;
    }
}
