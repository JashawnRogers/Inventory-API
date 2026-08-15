package com.jashawn.inventory_api.category.web;

import com.jashawn.inventory_api.category.dto.CategoryResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class CategoryAssembler implements RepresentationModelAssembler<CategoryResponse, EntityModel<CategoryResponse>> {

    @Override
    public EntityModel<CategoryResponse> toModel(@NonNull CategoryResponse entity) {
        EntityModel<CategoryResponse> model = EntityModel.of(entity,
                    linkTo(methodOn(CategoryController.class).findCategory(entity.id())).withSelfRel()
                );

        if (entity.isActive()) {
            model.add(linkTo(methodOn(CategoryController.class).deleteCategory(entity.id())).withRel("delete"));
        }

        return model;
    }
}
