package org.springboot.java17.api.inventory.controller;

import org.springboot.java17.api.inventory.dto.ItemDTO;
import org.springboot.java17.api.inventory.model.Item;
import org.springboot.java17.api.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
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
	public ItemDTO addItem(ItemDTO itemDTO) {
		ItemDTO item = null;
		if(itemDTO == null || itemDTO.getName() == null) {
			log.warn("Item name is null");
			return inventoryService.addItem(null);
		}
		item = inventoryService.addItem(itemDTO);		
		return item;
	}
}
