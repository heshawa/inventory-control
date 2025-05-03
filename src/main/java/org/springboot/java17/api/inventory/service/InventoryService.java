package org.springboot.java17.api.inventory.service;

import java.util.List;

import org.springboot.java17.api.inventory.dto.ItemDTO;

public interface InventoryService {
	ItemDTO addItem(ItemDTO item);
	
	List<ItemDTO> getItemsByName(String itemName) throws Exception;
	
	List<ItemDTO> getAllItems() throws Exception;
}
