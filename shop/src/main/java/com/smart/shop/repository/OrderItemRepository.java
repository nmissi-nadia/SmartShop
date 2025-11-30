package com.smart.shop.repository;

import com.smart.shop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
    List<OrderItem> findByCommandeId(String commandeId);
    void deleteByCommandeId(String commandeId);
}
