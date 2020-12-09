package com.example.intellekta25.jpa;

import com.example.intellekta25.entity.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface SalesJPA extends JpaRepository<Sales, Long> {
    public List<Sales> findByPriceGreaterThan(long price);
}
