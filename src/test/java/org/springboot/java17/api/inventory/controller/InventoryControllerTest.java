package org.springboot.java17.api.inventory.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springboot.java17.api.inventory.TestConstantValues;
import org.springboot.java17.api.inventory.dto.ItemDTO;
import org.springboot.java17.api.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(InventoryController.class)
public class InventoryControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private InventoryService service;
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Test
	public void shouldAddItem_whenValidItemIsPassedToService() throws Exception {

		ItemDTO responseDTO = new ItemDTO();
		responseDTO.setName(TestConstantValues.ITEM_SLEDGE_HAMMER_NAME);
		responseDTO.setDescription(TestConstantValues.ITEM_SLEDGE_HAMMER_DESCRIPTION);
		responseDTO.setPrice(TestConstantValues.ITEM_SLEDGE_HAMMER_PRICE);
		responseDTO.setQuantity(TestConstantValues.ITEM_SLEDGE_HAMMER_QUANTITY);


//		when(service.addItem(itemDTO)).thenReturn(responseDTO);
		
		when(service.addItem(argThat(itemDto -> 
				TestConstantValues.ITEM_SLEDGE_HAMMER_NAME.equals(itemDto.getName()) && 
						TestConstantValues.ITEM_SLEDGE_HAMMER_DESCRIPTION.equals(itemDto.getDescription()) && 
						TestConstantValues.ITEM_SLEDGE_HAMMER_PRICE.equals(itemDto.getPrice()) && 
						TestConstantValues.ITEM_SLEDGE_HAMMER_QUANTITY.equals(itemDto.getQuantity()))))
				.thenReturn(responseDTO);
		
//		when(service.addItem(any(ItemDTO.class))).thenReturn(responseDTO);
		
		mockMvc.perform(post("/inventory/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(responseDTO)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value(TestConstantValues.ITEM_SLEDGE_HAMMER_NAME))
				.andExpect(jsonPath("$.price").value(TestConstantValues.ITEM_SLEDGE_HAMMER_PRICE))
				.andExpect(jsonPath("$.quantity").value(TestConstantValues.ITEM_SLEDGE_HAMMER_QUANTITY))
				.andExpect(jsonPath("$.description").value(TestConstantValues.ITEM_SLEDGE_HAMMER_DESCRIPTION));
	}
	
	@Test
	public void shouldReturnNoContent_whenInvalidItemIsPassedToService() throws Exception {
		ItemDTO itemDTO = new ItemDTO();
		
		mockMvc.perform(post("/inventory/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(itemDTO)))
				.andExpect(status().isNoContent());
	}
	
	@Test
	public void shouldReturnResultResponse_whenThereAreItemsMatchingForSearchTerm() throws Exception {
		
		final String searchTerm = "hammer";

		ItemDTO sledgeHammer = new ItemDTO();
		sledgeHammer.setName(TestConstantValues.ITEM_SLEDGE_HAMMER_NAME);
		sledgeHammer.setDescription(TestConstantValues.ITEM_SLEDGE_HAMMER_DESCRIPTION);
		sledgeHammer.setPrice(TestConstantValues.ITEM_SLEDGE_HAMMER_PRICE);
		sledgeHammer.setQuantity(TestConstantValues.ITEM_SLEDGE_HAMMER_QUANTITY);

		ItemDTO nailHammer = new ItemDTO();
		nailHammer.setName(TestConstantValues.ITEM_NAIL_HAMMER_NAME);
		nailHammer.setDescription(TestConstantValues.ITEM_NAIL_HAMMER_DESCRIPTION);
		nailHammer.setPrice(TestConstantValues.ITEM_NAIL_HAMMER_PRICE);
		nailHammer.setQuantity(TestConstantValues.ITEM_NAIL_HAMMER_QUANTITY);


		when(service.getItemsByName(searchTerm)).thenReturn(List.of(sledgeHammer,nailHammer));
		
		mockMvc.perform(get("/inventory/"+searchTerm))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value(TestConstantValues.ITEM_SLEDGE_HAMMER_NAME))
				.andExpect(jsonPath("$[0].price").value(TestConstantValues.ITEM_SLEDGE_HAMMER_PRICE))
				.andExpect(jsonPath("$[0].quantity").value(TestConstantValues.ITEM_SLEDGE_HAMMER_QUANTITY))
				.andExpect(jsonPath("$[0].description").value(TestConstantValues.ITEM_SLEDGE_HAMMER_DESCRIPTION))
				.andExpect(jsonPath("$[1].name").value(TestConstantValues.ITEM_NAIL_HAMMER_NAME))
				.andExpect(jsonPath("$[1].price").value(TestConstantValues.ITEM_NAIL_HAMMER_PRICE))
				.andExpect(jsonPath("$[1].quantity").value(TestConstantValues.ITEM_NAIL_HAMMER_QUANTITY))
				.andExpect(jsonPath("$[1].description").value(TestConstantValues.ITEM_NAIL_HAMMER_DESCRIPTION));
	}
	
	@Test
	public void shouldReturnNoContent_whenThereAreNoItemsMatchingForSearchTerm() throws Exception {

		final String searchTerm = "aaa";
		
		when(service.getItemsByName(searchTerm)).thenReturn(List.of());
		
		mockMvc.perform(get("/inventory/"+searchTerm))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("No items with the given name"));
	}
}
