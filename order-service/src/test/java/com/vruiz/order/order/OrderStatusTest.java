package com.vruiz.order.order;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OrderStatusTest {

    @Test
    void createdCanMoveToInTransitOrCancelled() {
        assertTrue(OrderStatus.CREATED.canMoveTo(OrderStatus.IN_TRANSIT));
        assertTrue(OrderStatus.CREATED.canMoveTo(OrderStatus.CANCELLED));
        assertFalse(OrderStatus.CREATED.canMoveTo(OrderStatus.DELIVERED));
    }

    @Test
    void inTransitCanMoveToDeliveredOrCancelled() {
        assertTrue(OrderStatus.IN_TRANSIT.canMoveTo(OrderStatus.DELIVERED));
        assertTrue(OrderStatus.IN_TRANSIT.canMoveTo(OrderStatus.CANCELLED));
        assertFalse(OrderStatus.IN_TRANSIT.canMoveTo(OrderStatus.CREATED));
    }

    @Test
    void finalStatesCannotMove() {
        for (OrderStatus next : OrderStatus.values()) {
            assertFalse(OrderStatus.DELIVERED.canMoveTo(next));
            assertFalse(OrderStatus.CANCELLED.canMoveTo(next));
        }
    }
}
