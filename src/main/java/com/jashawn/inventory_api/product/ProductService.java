package com.jashawn.inventory_api.product;

import com.jashawn.inventory_api.Exceptions.DuplicateResourceException;
import com.jashawn.inventory_api.Exceptions.InactiveResourceException;
import com.jashawn.inventory_api.Exceptions.ResourceNotFoundException;
import com.jashawn.inventory_api.category.Category;
import com.jashawn.inventory_api.category.CategoryRepository;
import com.jashawn.inventory_api.category.dto.CategoryDtoMapper;
import com.jashawn.inventory_api.category.dto.CategoryResponse;
import com.jashawn.inventory_api.product.dto.CreateProductRequest;
import com.jashawn.inventory_api.product.dto.ProductDtoMapper;
import com.jashawn.inventory_api.product.dto.ProductResponse;
import com.jashawn.inventory_api.supplier.Supplier;
import com.jashawn.inventory_api.supplier.SupplierRepository;
import com.jashawn.inventory_api.supplier.dto.SupplierDtoMapper;
import com.jashawn.inventory_api.supplier.dto.SupplierResponse;
import org.springframework.dao.DataIntegrityViolationException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find category with ID: " + request.categoryId()));

        Supplier supplier = supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find supplier with ID: " + request.supplierId()));

        if (!category.isActive()) {
            throw new InactiveResourceException
                    ("The " + category.getName() + " category is inactive. Make the category active or select a different category.");
        }

        if (!supplier.isActive()) {
            throw new InactiveResourceException
                    ("The supplier, " + supplier.getName() + ", is inactive. Make the supplier active or select a different supplier");
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
        CategoryResponse categoryDto = CategoryDtoMapper.toDto(category);
        SupplierResponse supplierDto = SupplierDtoMapper.toDto(supplier);

        return ProductDtoMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponse findProduct(UUID id) {
       return productRepository.findById(id)
                .map(product -> {
                    CategoryResponse category = CategoryDtoMapper.toDto(product.getCategory());
                    SupplierResponse supplier = SupplierDtoMapper.toDto(product.getSupplier());

                    return ProductDtoMapper.toDto(product);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find product with ID: " + id));
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
    public ProductResponse updateProductName(UUID id, String name) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find product with ID: " + id));

        String newProductName = name.trim();

        if (product.getName().equals(newProductName)) {
            return ProductDtoMapper.toDto(product);
        }

        // Check before making call to update DB
        if (productRepository.findByName(newProductName).isPresent()) {
            throw new DuplicateResourceException("The name " + newProductName + " is already in use.");
        }

        product.updateName(newProductName);

            Product saved = productRepository.save(product);
            return ProductDtoMapper.toDto(saved);
    }

    @Transactional
    public ProductResponse updateProductReorderPoint(UUID id, int reorderPoint) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find product with ID: " + id));

        product.updateReorderPoint(reorderPoint);

        Product saved = productRepository.save(product);

        return ProductDtoMapper.toDto(saved);
    }

    @Transactional
    public ProductResponse updateProductDescription(UUID id, String description) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find product with ID: " + id));

        product.updateDescription(description);

        Product saved = productRepository.save(product);

        return ProductDtoMapper.toDto(saved);
    }

    @Transactional
    public ProductResponse updateProductUnitCost(UUID id, BigDecimal unitCost) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find product with ID: " + id));

        product.updateUnitCost(unitCost);

        Product saved = productRepository.save(product);

        return ProductDtoMapper.toDto(saved);
    }

    @Transactional
    public void softDelete(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot not find product with ID: " + id));

        product.softDelete();

        productRepository.save(product);
    }
}
