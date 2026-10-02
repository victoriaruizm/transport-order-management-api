package com.vruiz.driver;

import jakarta.validation.constraints.NotBlank;

public record DriverRequest(@NotBlank String name, @NotBlank String licenseNumber, Boolean active) {
}
