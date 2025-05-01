package org.springboot.java17.api.inventory.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Item, Integer> {

	List<Item> findByNameContainingIgnoreCase(String itemName);
}
