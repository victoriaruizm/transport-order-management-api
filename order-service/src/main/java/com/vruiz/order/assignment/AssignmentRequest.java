package com.vruiz.order.assignment;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AssignmentRequest(@NotNull UUID orderId, @NotNull UUID driverId) {
}
