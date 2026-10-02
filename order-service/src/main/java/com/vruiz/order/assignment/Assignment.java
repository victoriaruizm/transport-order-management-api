package com.vruiz.order.assignment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.UUID;

@Entity
public class Assignment {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID orderId;

    @Column(nullable = false)
    private UUID driverId;

    protected Assignment() {
    }

    public Assignment(UUID orderId, UUID driverId) {
        this.orderId = orderId;
        this.driverId = driverId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getDriverId() {
        return driverId;
    }
}
