package com.example.inventory.service;

import com.example.inventory.model.Product;
import com.example.inventory.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void reportsAvailableWhenStockCoversRequestedQuantity() {
        UUID productId = UUID.randomUUID();
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(new Product(productId, "Book", 5)));

        assertTrue(inventoryService.hasSufficientStock(productId, 5));
        assertFalse(inventoryService.hasSufficientStock(productId, 6));
    }

    @Test
    void reportsUnavailableForMissingProductsOrNonPositiveQuantity() {
        UUID productId = UUID.randomUUID();
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertFalse(inventoryService.hasSufficientStock(productId, 1));
        assertFalse(inventoryService.hasSufficientStock(productId, 0));
    }

    @Test
    void reservesStockAndEvictsCachedProduct() {
        UUID productId = UUID.randomUUID();
        Cache productCache = mock(Cache.class);
        when(productRepository.reserveAvailableStock(productId, 3)).thenReturn(1);
        when(cacheManager.getCache("products")).thenReturn(productCache);

        assertTrue(inventoryService.reserveStock(productId, 3));

        verify(productCache).evict(productId);
    }
}
