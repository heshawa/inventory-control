package org.springboot.java17.api.inventory.service;

import java.math.BigDecimal;

import org.springboot.java17.api.inventory.dto.ItemDTO;
import org.springboot.java17.api.inventory.model.Item;
import org.springframework.stereotype.Service;

@Service//Helps to auto-wire as a bean
public class InventoryServiceImpl implements InventoryService {

	@Override
	public ItemDTO addItem(ItemDTO item) {
		// Logic to add creatingItem to inventory
		Item creatingItem = null;
		if(creatingItem == null) {
			creatingItem = new Item();
			creatingItem.setName("Sample Item");
			creatingItem.setDescription("This is a sample item.");
			creatingItem.setPrice(new BigDecimal("19.99"));
			creatingItem.setQuantity(Integer.parseInt("10"));
		}
		ItemDTO createdItem = new ItemDTO();
		try{
			createdItem.setName(creatingItem.getName());
			createdItem.setDescription(creatingItem.getDescription());
			createdItem.setPrice(String.valueOf(creatingItem.getPrice()));
			createdItem.setQuantity(String.valueOf(creatingItem.getQuantity()));
		}catch (Exception e) {
			return null;
		}
		
		return createdItem;
	}

	// Additional methods for inventory management can be added here
}
