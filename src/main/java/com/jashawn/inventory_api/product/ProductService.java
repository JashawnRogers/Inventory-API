package com.jashawn.inventory_api.product;

import com.jashawn.inventory_api.Exceptions.DuplicateResourceException;
import com.jashawn.inventory_api.Exceptions.InactiveResourceException;
import com.jashawn.inventory_api.Exceptions.ResourceNotFoundException;
import com.jashawn.inventory_api.category.Category;
import com.jashawn.inventory_api.category.CategoryRepository;
import com.jashawn.inventory_api.product.dto.CreateProductRequest;
import com.jashawn.inventory_api.product.dto.ProductDtoMapper;
import com.jashawn.inventory_api.product.dto.ProductResponse;
import com.jashawn.inventory_api.product.dto.UpdateProductRequest;
import com.jashawn.inventory_api.supplier.Supplier;
import com.jashawn.inventory_api.supplier.SupplierRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SupplierRepository supplierRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.supplierRepository = supplierRepository;
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "ID",  request.categoryId().toString()));

        Supplier supplier = supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "ID",  request.supplierId().toString()));

        if (!category.isActive()) {
            throw new InactiveResourceException(request.name(), "Category: " + category.getName());
        }

        if (!supplier.isActive()) {
            throw new InactiveResourceException(request.name(), "Supplier: " + supplier.getName());
        }

        Product product = Product.of(
                request.name(),
                request.description(),
                request.unitCost(),
                request.reorderPoint(),
                category,
                supplier
        );

        Product saved = productRepository.save(product);

        return ProductDtoMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponse findProduct(UUID id) {
       return productRepository.findById(id)
                .map(ProductDtoMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", id.toString()));
    }


    @Transactional(readOnly = true)
    public Page<ProductResponse> findAllProducts(
            Boolean status,
            String name,
            UUID sku,
            BigDecimal unitCost,
            PageRequest request
    ) {

        Specification<Product> spec = Specification
                .where(ProductSpecifications.isNotDeleted())
                .and(ProductSpecifications.hasStatus(status))
                .and(ProductSpecifications.hasName(name))
                .or(ProductSpecifications.hasSku(sku))
                .or(ProductSpecifications.hasUnitCost(unitCost));

        return productRepository.findAll(spec, request)
                .map(ProductDtoMapper::toDto);
    }

    @Transactional
    public ProductResponse updateProduct(UUID id, UpdateProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", id.toString()));

        if (request.name() != null) {
            updateProductName(product, request.name());
        }

        if (request.description() != null) {
            product.updateDescription(request.description());
        }

        if (request.unitCost() != null) {
            product.updateUnitCost(request.unitCost());
        }

        if (request.reorderPoint() != null) {
            product.updateReorderPoint(request.reorderPoint());
        }

        if (request.categoryId() != null) {
            Category category = categoryRepository.findById(request.categoryId())
                            .orElseThrow(() -> new ResourceNotFoundException("Category", "ID", request.categoryId().toString()));

            product.updateCategory(category);
        }

        if (request.supplierId() != null) {
            Supplier supplier = supplierRepository.findById(request.supplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier", "ID", request.supplierId().toString()));

            product.updateSupplier(supplier);
        }

        Product saved = productRepository.save(product);

        return ProductDtoMapper.toDto(saved);
    }

    private void updateProductName(Product product, String name) {
        String newProductName = name.trim();

        // Check before writing to DB
        if (product.getName().equals(newProductName) ||
                productRepository.findByName(newProductName).isPresent()
        ) {
            throw new DuplicateResourceException("Product", "name", newProductName);
        }

        product.updateName(newProductName);
    }

    @Transactional
    public void softDelete(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "ID", id.toString()));

        product.softDelete();

        productRepository.save(product);
    }
}
