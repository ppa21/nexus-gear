package com.nexusgear.controller;

import com.nexusgear.dto.OrderRequest;
import com.nexusgear.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderRequest orderRequest, Principal principal) {
        orderService.placeOrder(principal.getName(), orderRequest);
        return ResponseEntity.ok("Order placed successfully");
    }

    @GetMapping
    public ResponseEntity<?> getUserOrders(Principal principal) {
        return ResponseEntity.ok(orderService.getUserOrders(principal.getName()));
    }
}
