package com.vruiz.order.assignment;

import java.util.UUID;

public record AssignmentResponse(UUID id, UUID orderId, UUID driverId) {

    public static AssignmentResponse from(Assignment a) {
        return new AssignmentResponse(a.getId(), a.getOrderId(), a.getDriverId());
    }
}
