package org.springboot.java17.api.inventory.controller;

import org.springboot.java17.api.inventory.dto.ItemDTO;
import org.springboot.java17.api.inventory.model.Item;
import org.springboot.java17.api.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
@ResponseBody//Returns json object
public class InventoryController {
	
	@Autowired
	private InventoryService inventoryService;
	@PostMapping("/add")
	public ItemDTO addItem(ItemDTO itemDTO) {
		ItemDTO item = inventoryService.addItem(null);		
		return item;
	}
}
