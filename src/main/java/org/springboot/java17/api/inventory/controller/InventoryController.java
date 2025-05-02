package org.springboot.java17.api.inventory.controller;

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
		return ResponseEntity.ok(item);
	}
	
	@GetMapping("/{itemName}")
	public ResponseEntity getItems(@PathVariable String itemName){
		try {
			List<ItemDTO> items = inventoryService.getItemsByName(itemName);
			log.debug("Items found for the given value. value: {}", itemName);
			if(CollectionUtils.isEmpty(items)){
				log.warn("No items found for the given value. value: {}", itemName);
				return ResponseEntity.ok(new ResponseMessage("No items with the given name",""));
			}
			return ResponseEntity.ok(items);
		} catch (Exception e) {
			return ResponseEntity.internalServerError().body(new ResponseMessage("Error while fetching items",e.getMessage()));
		}
	}
}
