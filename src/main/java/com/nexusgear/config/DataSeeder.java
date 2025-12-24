package com.nexusgear.config;

import com.nexusgear.domain.*;
import com.nexusgear.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final OrderRepository orderRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        // 1. Clean up database in correct order (Child -> Parent)
        orderRepo.deleteAll();   // Delete orders first (they refer to users)
        productRepo.deleteAll(); // Delete products
        userRepo.deleteAll();    // Delete users last

        // Create Admin (email: admin@nexus.com / pass: admin123)
        if (userRepo.findByEmail("admin").isEmpty()) {
            userRepo.save(User.builder().email("admin")
                    .password(encoder.encode("123")).role(Role.ADMIN).build());
        }
        // Create 20 Dummy Products
        if (productRepo.count() == 0) {
            String[] gadgets = {"iPhone 15", "MacBook Pro", "Sony Headphones", "Gaming Mouse", "4K Monitor"};
            for (int i = 0; i < 20; i++) {
                String name = gadgets[i % gadgets.length] + " " + (i + 1);
                productRepo.save(Product.builder()
                        .name(name)
                        .description("The best gadget ever.")
                        .price(new BigDecimal("100").add(new BigDecimal(i * 10)))
                        .stock(10) // 10 items in stock
                        .imageUrl("https://placehold.co/300?text=" + name.replace(" ", "+"))
                        .build());
            }
        }
    }
}