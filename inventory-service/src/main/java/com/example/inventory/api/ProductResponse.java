package com.example.inventory.api;

import com.example.inventory.model.Product;

import java.util.UUID;

public record ProductResponse(UUID id, String name, int availableQuantity) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getAvailableQuantity());
    }
}
