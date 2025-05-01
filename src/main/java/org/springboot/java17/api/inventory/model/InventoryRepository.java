package org.springboot.java17.api.inventory.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Item, Integer> {
}
