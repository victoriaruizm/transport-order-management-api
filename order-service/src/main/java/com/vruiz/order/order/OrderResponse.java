package com.vruiz.order.order;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResponse(UUID id, OrderStatus status, String origin, String destination,
                            LocalDateTime createdAt, LocalDateTime updatedAt) {

    public static OrderResponse from(Order o) {
        return new OrderResponse(o.getId(), o.getStatus(), o.getOrigin(), o.getDestination(),
                o.getCreatedAt(), o.getUpdatedAt());
    }
}
