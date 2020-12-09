package com.example.intellekta25.jpa;

import com.example.intellekta25.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductJpa extends JpaRepository<Product, Long> {
}
