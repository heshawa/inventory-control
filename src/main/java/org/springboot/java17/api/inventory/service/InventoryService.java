package org.springboot.java17.api.inventory.service;

import java.util.List;

import org.springboot.java17.api.common.dto.ItemDTO;

public interface InventoryService {
	ItemDTO addItem(ItemDTO item);
	
	List<ItemDTO> getItemsByName(String itemName) throws Exception;
	
	List<ItemDTO> getAllItems() throws Exception;

	List<ItemDTO> getItemsByIds(List<Integer> itemIds) throws Exception;

	List<ItemDTO> allocateInventory(List<ItemDTO> items) throws Exception;
}
