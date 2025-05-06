package org.springboot.java17.api.inventory.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springboot.java17.api.common.dto.ItemDTO;
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
			creatingItem = convertToItem(item);
		}

		try{
			creatingItem = inventoryRepository.save(creatingItem);
		}catch (Exception e) {
			log.error("Error while creating item", e);
			return null;
		}
		
		return convertToItemDTO(creatingItem);
	}

	@Override
	public List<ItemDTO> getItemsByName(String itemName) throws Exception{
		if(StringUtils.isEmpty(itemName)){
			log.warn("Search item name is not provided");
			return null;
		}
		
		List<Item> items = inventoryRepository.findByNameContainingIgnoreCase(itemName);
		
		if(!CollectionUtils.isEmpty(items)){
			return items.stream().map(item ->convertToItemDTO(item)).collect(Collectors.toList());
		}

		return null;
	}

	@Override
	public List<ItemDTO> getAllItems() throws Exception {
		List<Item> items = inventoryRepository.findAll();
		return items.stream().map(item -> convertToItemDTO(item)).collect(Collectors.toList());
	}

	@Override
	public List<ItemDTO> getItemsByIds(List<Integer> itemIds) throws Exception{
		if(CollectionUtils.isEmpty(itemIds)){
			log.warn("Item ids are not provided");
			return null;
		}
		
		List<Item> items = inventoryRepository.findAllById(itemIds);
		return items.stream().map(item->convertToItemDTO(item)).collect(Collectors.toList());
	}

	@Override
	public List<ItemDTO> allocateInventory(List<ItemDTO> items) throws Exception{
		if(CollectionUtils.isEmpty(items)){
			log.warn("No items were sent for allocation");
			return Collections.emptyList();
		}

		List<Integer> itemsToFetch = items.stream().map(ItemDTO::getId).collect(Collectors.toList());
		List<Item> itemsToAllocate = inventoryRepository.findAllById(itemsToFetch);
		
		List<Integer> notFoundOrders = new ArrayList();

		items.stream().forEach(orderLineItem -> {
			Item itemToAllocate = itemsToAllocate.stream().filter(item -> orderLineItem.getId()==item.getId()).findFirst().orElse(null);

			if(itemToAllocate == null){
				notFoundOrders.add(orderLineItem.getId());
				log.warn("Invalid item ID. ItemId: {}",orderLineItem.getId());
				orderLineItem.setPrice("0");
				orderLineItem.setName("");
				orderLineItem.setDescription("");
				orderLineItem.setId(-1);
				return;
			}

			if(itemToAllocate.getQuantity()<Integer.parseInt(orderLineItem.getQuantity())){
				orderLineItem.setQuantity("-1");
				log.warn("Not enough stocks for allocation. Item ID: {}, Item Name: {}, Item Count: {}, Order Count: {}",
						itemToAllocate.getId(), itemToAllocate.getName(), itemToAllocate.getQuantity(), orderLineItem.getQuantity());
			}else{
				itemToAllocate.setQuantity(itemToAllocate.getQuantity()-Integer.parseInt(orderLineItem.getQuantity()));
			}
			orderLineItem.setName(itemToAllocate.getName());
			orderLineItem.setDescription(itemToAllocate.getDescription());
			orderLineItem.setPrice(String.valueOf(itemToAllocate.getPrice()));
		});

		if(!CollectionUtils.isEmpty(notFoundOrders)){
			log.warn("Entire order was not able to reserve. Invalid orders: {}",Arrays.toString(notFoundOrders.toArray()));
		}

		inventoryRepository.saveAll(itemsToAllocate);
		itemsToFetch.removeAll(notFoundOrders);
		
		log.info("Order items allocation is successful. Item Ids: {}", Arrays.toString(itemsToFetch.toArray()));
		return items;
	}

	private ItemDTO convertToItemDTO(Item item){
		if(item == null){
			log.warn("Item is null");
			return null;
		}
		ItemDTO itemDTO = new ItemDTO();
		itemDTO.setName(item.getName());
		itemDTO.setDescription(item.getDescription());
		itemDTO.setPrice(String.valueOf(item.getPrice()));
		itemDTO.setQuantity(String.valueOf(item.getQuantity()));
		itemDTO.setId(item.getId());
		
		return itemDTO;
	}
	
	private Item convertToItem(ItemDTO itemDTO){
		if(itemDTO == null){
			log.warn("ItemDTO is null");
			return null;
		}
		Item item = new Item();
		item.setName(itemDTO.getName());
		item.setDescription(itemDTO.getDescription());
		item.setPrice(new BigDecimal(itemDTO.getPrice()));
		item.setQuantity(Integer.parseInt(itemDTO.getQuantity()));
		item.setId(itemDTO.getId());

		return item;
	}
}
