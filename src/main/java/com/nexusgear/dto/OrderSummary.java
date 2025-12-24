package com.nexusgear.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummary(Long id, LocalDateTime date, BigDecimal total, String status) {
}
