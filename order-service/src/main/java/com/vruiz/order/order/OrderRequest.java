package com.vruiz.order.order;

import jakarta.validation.constraints.NotBlank;

public record OrderRequest(@NotBlank String origin, @NotBlank String destination) {
}
