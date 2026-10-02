package com.vruiz.driver;

import java.util.UUID;

public record DriverResponse(UUID id, String name, String licenseNumber, boolean active) {

    public static DriverResponse from(Driver d) {
        return new DriverResponse(d.getId(), d.getName(), d.getLicenseNumber(), d.isActive());
    }
}
