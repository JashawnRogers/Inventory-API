package com.jashawn.inventory_api.category;

import com.jashawn.inventory_api.Exceptions.InvalidFieldException;
import com.jashawn.inventory_api.Exceptions.ResourceNotFoundException;
import com.jashawn.inventory_api.category.dto.CategoryDtoMapper;
import com.jashawn.inventory_api.category.dto.CategoryResponse;
import com.jashawn.inventory_api.category.dto.CreateCategoryRequest;
import com.jashawn.inventory_api.category.dto.UpdateCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public CategoryResponse findCategoryById(UUID id) {
        if (id == null) {
            throw new InvalidFieldException("Category", "ID", "null");
        }

        return categoryRepository.findById(id)
                .map(CategoryDtoMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "ID",  id.toString()));
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> findAllCategories(String name, Boolean status, PageRequest pageRequest) {
        Specification<Category> spec = Specification.where(CategorySpecifications.isNotDeleted())
                .and(CategorySpecifications.hasName(name))
                .or(CategorySpecifications.hasStatus(status));

        return categoryRepository.findAll(spec, pageRequest)
                .map(CategoryDtoMapper::toDto);
    }

    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category category = Category.create(request.name(), request.description());

        Category saved = categoryRepository.save(category);

        return CategoryDtoMapper.toDto(saved);
    }

    @Transactional
    public CategoryResponse updateCategory(UUID id, UpdateCategoryRequest dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "ID", id.toString()));

        if (dto.name() != null) {
            category.updateName(dto.name());
        }

        if (dto.description() != null) {
            category.updateDescription(dto.description());
        }

        if (dto.status() != null) {
            if (dto.status()) {
                category.activate();
            } else {
                category.deactivate();
            }
        }

        Category saved = categoryRepository.save(category);

        return CategoryDtoMapper.toDto(saved);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "ID", id.toString()));

        category.softDelete();

        categoryRepository.save(category);
    }

}
