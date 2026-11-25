package com.example.inventory.service;

import com.example.events.InventoryRejectedEvent;
import com.example.events.InventoryReservedEvent;
import com.example.events.OrderCreatedEvent;
import com.example.inventory.api.CreateProductRequest;
import com.example.inventory.api.ProductResponse;
import com.example.inventory.model.Product;
import com.example.inventory.repository.ProductRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class InventoryService {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CacheManager cacheManager;

    public InventoryService(
            ProductRepository productRepository,
            ApplicationEventPublisher applicationEventPublisher,
            CacheManager cacheManager
    ) {
        this.productRepository = productRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.cacheManager = cacheManager;
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = new Product(UUID.randomUUID(), request.name(), request.availableQuantity());
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public ProductResponse getProduct(UUID id) {
        return productRepository.findById(id)
                .map(ProductResponse::from)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found"));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts() {
        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean hasSufficientStock(UUID productId, int quantity) {
        return quantity > 0 && productRepository.findById(productId)
                .map(product -> product.getAvailableQuantity() >= quantity)
                .orElse(false);
    }

    @Transactional
    public boolean reserveStock(UUID productId, int quantity) {
        if (quantity <= 0 || productRepository.reserveAvailableStock(productId, quantity) != 1) {
            return false;
        }
        Cache cache = cacheManager.getCache("products");
        if (cache == null) {
            throw new IllegalStateException("Product cache is not configured");
        }
        cache.evict(productId);
        return true;
    }

    @Transactional
    public void reserveOrder(OrderCreatedEvent event) {
        if (event.productId() == null) {
            publishRejection(event, "Invalid product ID");
            return;
        }

        UUID productId;
        try {
            productId = UUID.fromString(event.productId());
        } catch (IllegalArgumentException e) {
            publishRejection(event, "Invalid product ID");
            return;
        }

        if (reserveStock(productId, event.quantity())) {
            applicationEventPublisher.publishEvent(new InventoryReservedEvent(
                    UUID.randomUUID(),
                    event.orderId(),
                    event.productId(),
                    event.quantity(),
                    event.amount(),
                    Instant.now()
            ));
        } else {
            publishRejection(event, "Product not found or insufficient stock");
        }
    }

    private void publishRejection(OrderCreatedEvent event, String reason) {
        applicationEventPublisher.publishEvent(new InventoryRejectedEvent(
                UUID.randomUUID(),
                event.orderId(),
                event.productId(),
                event.quantity(),
                reason,
                Instant.now()
        ));
    }

}
