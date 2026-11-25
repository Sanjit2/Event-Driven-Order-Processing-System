package com.example.inventory.repository;

import com.example.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Product product
            set product.availableQuantity = product.availableQuantity - :quantity
            where product.id = :id and product.availableQuantity >= :quantity
            """)
    int reserveAvailableStock(@Param("id") UUID id, @Param("quantity") int quantity);
}
