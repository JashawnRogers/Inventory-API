package com.jashawn.inventory_api.product.web;

import com.jashawn.inventory_api.product.ProductService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }
}
