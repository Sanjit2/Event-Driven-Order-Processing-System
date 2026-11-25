package com.example.inventory.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateProductRequest(
        @NotBlank String name,
        @PositiveOrZero int availableQuantity
) {
}
