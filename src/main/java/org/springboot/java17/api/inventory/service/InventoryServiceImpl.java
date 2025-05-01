package org.springboot.java17.api.inventory.service;

import java.math.BigDecimal;

import org.springboot.java17.api.inventory.dto.ItemDTO;
import org.springboot.java17.api.inventory.model.Item;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service//Helps to auto-wire as a bean
public class InventoryServiceImpl implements InventoryService {

	@Override
	public ItemDTO addItem(ItemDTO item) {
		// Logic to add creatingItem to inventory
		Item creatingItem = null;
		if(item == null) {
			log.warn("Item is null");
		} else {
			creatingItem = new Item();
			creatingItem.setName(item.getName());
			creatingItem.setDescription(item.getDescription());
			creatingItem.setPrice(new BigDecimal(item.getPrice()));
			creatingItem.setQuantity(Integer.parseInt(item.getQuantity()));
		}
		ItemDTO createdItem = new ItemDTO();
		try{
			createdItem.setName(creatingItem.getName());
			createdItem.setDescription(creatingItem.getDescription());
			createdItem.setPrice(String.valueOf(creatingItem.getPrice()));
			createdItem.setQuantity(String.valueOf(creatingItem.getQuantity()));
		}catch (Exception e) {
			log.error("Error while creating item", e);
			return null;
		}
		
		return createdItem;
	}

	// Additional methods for inventory management can be added here
}
