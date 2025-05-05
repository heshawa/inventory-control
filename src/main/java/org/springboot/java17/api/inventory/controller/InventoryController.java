package org.springboot.java17.api.inventory.controller;

import java.util.Arrays;
import java.util.List;

import org.springboot.java17.api.ResponseMessage;
import org.springboot.java17.api.inventory.dto.ItemDTO;
import org.springboot.java17.api.inventory.model.Item;
import org.springboot.java17.api.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/inventory")
@ResponseBody//Returns json object
public class InventoryController {
	
	@Autowired
	private InventoryService inventoryService;
	@PostMapping("/add")
	public ResponseEntity addItem(@RequestBody ItemDTO itemDTO) {
		ItemDTO item = null;
		if(itemDTO == null || itemDTO.getName() == null) {
			log.warn("Item name is null");
			return ResponseEntity.noContent().build();
		}
		item = inventoryService.addItem(itemDTO);
		ResponseMessage message = new ResponseMessage("");
		message.getData().add(item);
		return ResponseEntity.ok(message);
	}
	
	@GetMapping("/{itemName}")
	public ResponseEntity getItems(@PathVariable String itemName){
		try {
			List<ItemDTO> items = inventoryService.getItemsByName(itemName);
			log.debug("Items found for the given value. value: {}", itemName);
			if(CollectionUtils.isEmpty(items)){
				log.warn("No items found for the given value. value: {}", itemName);
				return ResponseEntity.ok(new ResponseMessage("No items with the given name."));
			}
			ResponseMessage message = new ResponseMessage("");
			message.setData(items);
			return ResponseEntity.ok(message);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(new ResponseMessage("Error while fetching items. " + e.getMessage()));
		}
	}
	
	@GetMapping("")
	public ResponseEntity getAllItems(){
		try {
			ResponseMessage message = null;
			List<ItemDTO> allItems = inventoryService.getAllItems();
			
			if(CollectionUtils.isEmpty(allItems)){
				message = new ResponseMessage("No items available.");
			}else {
				message = new ResponseMessage("");
				message.setData(allItems);
			}
			return ResponseEntity.ok(message);
		} catch (Exception e) {
			log.error("Error while fetching all items", e);
			return ResponseEntity.internalServerError().body(new ResponseMessage("Error while fetching all items. " + e.getMessage()));
		}
	}
	
	@PostMapping("/items")
	public ResponseEntity getItemsForItemIds(@RequestBody List<Integer> itemIds){
		if(CollectionUtils.isEmpty(itemIds)){
			log.warn("Item ids are empty");
			return ResponseEntity.badRequest().body(new ResponseMessage("Item ids are empty."));
		}
		List<ItemDTO> items = null;
		try {
			items = inventoryService.getItemsByIds(itemIds);
			if(CollectionUtils.isEmpty(items)){
				log.warn("No items found for the given ids");
				return ResponseEntity.ok(new ResponseMessage("No items found for the given ids."));
			}
		} catch (Exception e) {
			log.warn("Error while fetching items for the given ids. Ids: {}", Arrays.toString(itemIds.toArray()));
		}
		return ResponseEntity.ok(items);
	}
	
	@PostMapping("/allocate")
	public ResponseEntity allocateInventory(@RequestBody List<ItemDTO> items){
		if(CollectionUtils.isEmpty(items)){
			log.warn("No items were sent for allocations");
			return ResponseEntity.badRequest().body(new ResponseMessage("No items were sent for allocations."));
		}

		List<ItemDTO> allocatedItems = null;
		try {
			allocatedItems = inventoryService.allocateInventory(items);
			
			boolean isOrderPassed = allocatedItems.stream().filter(item->Integer.parseInt(item.getQuantity())>0).findFirst().isPresent();

			if(CollectionUtils.isEmpty(allocatedItems) || !isOrderPassed){
				log.warn("No items were allocated");
				ResponseMessage message = new ResponseMessage("No items were allocated.");
				message.setSuccess(false);
				return ResponseEntity.ok(message);
			}
		} catch (Exception e) {
			log.error("Unexpected error while allocating the order.",e);
			ResponseMessage message = new ResponseMessage("Unexpected Error while fulFilling the order. " + e.getMessage());
			message.setSuccess(false);
			return ResponseEntity.internalServerError().body(message);
		}

		ResponseMessage<ItemDTO> message = new ResponseMessage<>("");
		message.setData(allocatedItems);
		return ResponseEntity.ok(message);
	}
}
