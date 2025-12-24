package com.nexusgear.controller;

import com.nexusgear.domain.Product;
import com.nexusgear.dto.ProductRequest;
import com.nexusgear.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public Page<Product> getAll(Pageable pageable) {
        return productService.getAll(pageable);
    }

    @GetMapping("/search")
    public Page<Product> search(@RequestParam String query, Pageable pageable) {
        return productService.search(query, pageable);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Product create(@RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Product update(@PathVariable Long id, @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}
