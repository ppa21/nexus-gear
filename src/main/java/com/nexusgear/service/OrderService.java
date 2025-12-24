package com.nexusgear.service;

import com.nexusgear.domain.*;
import com.nexusgear.dto.*;
import com.nexusgear.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void placeOrder(String userEmail, OrderRequest orderRequest) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = Order.builder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();

//        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : orderRequest.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + itemRequest.productId()));

            if (product.getStock() < itemRequest.quantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            product.setStock(product.getStock() - itemRequest.quantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                            .productId(product.getId())
                            .productName(product.getName())
                            .priceAtPurchase(product.getPrice())
                            .quantity(itemRequest.quantity())
                            .build();

            order.getItems().add(orderItem);
            totalAmount = totalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())));
        }

        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
    }

    public List<OrderSummary> getUserOrders(String email) {
        return orderRepository.findByUserEmailOrderByCreatedAtDesc(email).stream()
                .map(order -> new OrderSummary(order.getId(), order.getCreatedAt(), order.getTotalAmount(), "CONFIRMED"))
                .toList();
    }
}