package com.vruiz.order.order;

public enum OrderStatus {
    CREATED, IN_TRANSIT, DELIVERED, CANCELLED;

    public boolean canMoveTo(OrderStatus next) {
        return switch (this) {
            case CREATED -> next == IN_TRANSIT || next == CANCELLED;
            case IN_TRANSIT -> next == DELIVERED || next == CANCELLED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}
