package com.nexusgear.service;

import com.nexusgear.domain.Product;
import com.nexusgear.dto.ProductRequest;
import com.nexusgear.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Page<Product> getAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> search(String query, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(query, pageable);
    }

    public Product create(ProductRequest request) {
        return productRepository.save(Product.builder()
                .name(request.name()).description(request.description())
                .price(request.price()).stock(request.stock()).imageUrl(request.imageUrl())
                .build());
    }

    public Product update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        if (request.imageUrl() != null) {
            product.setImageUrl(request.imageUrl());
        }
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
