package com.nexusgear.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @GeneratedValue(    strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    private LocalDateTime createdAt;

    private BigDecimal totalAmount;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name="order_id")
    private List<OrderItem> items;
}
