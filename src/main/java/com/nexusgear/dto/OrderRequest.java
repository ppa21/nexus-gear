package com.nexusgear.dto;

import java.util.List;

public record OrderRequest(List<OrderItemRequest> items) {
}
