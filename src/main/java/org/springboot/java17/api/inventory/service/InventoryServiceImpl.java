package org.springboot.java17.api.inventory.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springboot.java17.api.inventory.dto.ItemDTO;
import org.springboot.java17.api.inventory.model.InventoryRepository;
import org.springboot.java17.api.inventory.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service//Helps to auto-wire as a bean
public class InventoryServiceImpl implements InventoryService {

	@Autowired
	private InventoryRepository inventoryRepository;
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
			inventoryRepository.save(creatingItem);
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

	@Override
	public List<ItemDTO> getItemsByName(String itemName) throws Exception{
		if(StringUtils.isEmpty(itemName)){
			log.warn("Search item name is not provided");
			return null;
		}
		
		List<Item> items = inventoryRepository.findByNameContainingIgnoreCase(itemName);
		
		if(!CollectionUtils.isEmpty(items)){
			return items.stream().map(item ->{
				ItemDTO itemDto = new ItemDTO();
				itemDto.setName(item.getName());
				itemDto.setDescription(item.getDescription());
				itemDto.setPrice(String.valueOf(item.getPrice()));
				itemDto.setQuantity(String.valueOf(item.getQuantity()));
				return itemDto;
			}).collect(Collectors.toList());
		}

		return null;
	}
}
