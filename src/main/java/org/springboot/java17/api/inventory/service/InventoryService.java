package org.springboot.java17.api.inventory.service;

import org.springboot.java17.api.inventory.dto.ItemDTO;

public interface InventoryService {
	ItemDTO addItem(ItemDTO item);
}
