package org.springboot.java17.api.inventory.controller;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springboot.java17.api.inventory.TestConstantValues;
import org.springboot.java17.api.common.dto.ItemDTO;
import org.springboot.java17.api.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasSize;
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
	public void shouldAddItem_whenValidItemIsPassedOnItemsAddApiInvocation() throws Exception {

		ItemDTO responseDTO = getSledgeHammer();

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
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].name").value(TestConstantValues.ITEM_SLEDGE_HAMMER_NAME))
				.andExpect(jsonPath("$.data[0].price").value(TestConstantValues.ITEM_SLEDGE_HAMMER_PRICE))
				.andExpect(jsonPath("$.data[0].quantity").value(TestConstantValues.ITEM_SLEDGE_HAMMER_QUANTITY))
				.andExpect(jsonPath("$.data[0].description").value(TestConstantValues.ITEM_SLEDGE_HAMMER_DESCRIPTION));
	}
	
	@Test
	public void shouldReturnNoContent_whenInvalidItemIsPassedOnItemsAddApiInvocation() throws Exception {
		ItemDTO itemDTO = new ItemDTO();
		
		mockMvc.perform(post("/inventory/add")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(itemDTO)))
				.andExpect(status().isNoContent());
	}
	
	@Test
	public void shouldReturnResultResponse_whenThereAreItemsMatchingForSearchTermOnSearchApiInvocation() throws Exception {
		
		final String searchTerm = "hammer";

		ItemDTO sledgeHammer = getSledgeHammer();
		ItemDTO nailHammer = getNailHammer();

		when(service.getItemsByName(searchTerm)).thenReturn(List.of(sledgeHammer,nailHammer));
		
		mockMvc.perform(get("/inventory/"+searchTerm))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data[0].name").value(TestConstantValues.ITEM_SLEDGE_HAMMER_NAME))
				.andExpect(jsonPath("$.data[0].price").value(TestConstantValues.ITEM_SLEDGE_HAMMER_PRICE))
				.andExpect(jsonPath("$.data[0].quantity").value(TestConstantValues.ITEM_SLEDGE_HAMMER_QUANTITY))
				.andExpect(jsonPath("$.data[0].description").value(TestConstantValues.ITEM_SLEDGE_HAMMER_DESCRIPTION))
				.andExpect(jsonPath("$.data[1].name").value(TestConstantValues.ITEM_NAIL_HAMMER_NAME))
				.andExpect(jsonPath("$.data[1].price").value(TestConstantValues.ITEM_NAIL_HAMMER_PRICE))
				.andExpect(jsonPath("$.data[1].quantity").value(TestConstantValues.ITEM_NAIL_HAMMER_QUANTITY))
				.andExpect(jsonPath("$.data[1].description").value(TestConstantValues.ITEM_NAIL_HAMMER_DESCRIPTION));
	}

	@Test
	public void shouldReturnNoContent_whenThereAreNoItemsMatchingForSearchTermOnSearchApiInvocation() throws Exception {

		final String searchTerm = "aaa";

		when(service.getItemsByName(searchTerm)).thenReturn(List.of());

		mockMvc.perform(get("/inventory/"+searchTerm))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value("No items with the given name."));
	}

	@Test
	public void shouldReturnInternalServerErrorAndSuccessFalse_whenTheServiceIsNullOnSearchApiInvocation() throws Exception {

		final String searchTerm = "aaa";

		when(service.getItemsByName(searchTerm)).thenThrow(NullPointerException.class);

		mockMvc.perform(get("/inventory/"+searchTerm))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message",not(emptyOrNullString())));
	}

	@Test
	public void shouldReturnItemDetailsForGivenIds_whenThereAreValidItemIdsInRequestOnRetrieveItemsByIdInvocation() throws Exception {
		ItemDTO sledgeHammer = getSledgeHammer();
		ItemDTO nailHammer = getNailHammer();
		ItemDTO screwDriver = getScrewDriver();
		
		List<Integer> itemIdList = List.of(sledgeHammer.getId(),nailHammer.getId(),screwDriver.getId());
		when(service.getItemsByIds(argThat(itemIds->itemIds.contains(sledgeHammer.getId()) &&
				itemIds.contains(nailHammer.getId()) &&
				itemIds.contains(screwDriver.getId()))))
				.thenReturn(List.of(sledgeHammer,nailHammer,screwDriver));
		
		mockMvc.perform(post("/inventory/items")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemIdList)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value(""))
				.andExpect(jsonPath("$.data[0].name",not(emptyOrNullString())))
				.andExpect(jsonPath("$.data[1].name",not(emptyOrNullString())))
				.andExpect(jsonPath("$.data[1].name",not(emptyOrNullString())));
	}
	
	@Test
	public void shouldReturnSuccessFalse_whenNoItemIdsPassedOnRetrieveItemsByIdInvocation() throws Exception{
		List<Integer> itemIdList = Collections.emptyList();
		
		mockMvc.perform(post("/inventory/items")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(itemIdList))
		).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message",not(emptyOrNullString())));
	}
	
	@Test
	public void shouldReturnSuccessFalse_whenThereAreInvalidItemIdsInRequestOnRetrieveItemsByIdInvocation() throws Exception {
		List<Integer> itemIdList = List.of(1,2,3);
		
		when(service.getItemsByIds(argThat(itemIds->itemIds.contains(1) &&
				itemIds.contains(2) &&
				itemIds.contains(3))))
				.thenReturn(Collections.emptyList());

		mockMvc.perform(post("/inventory/items")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemIdList))
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message",not(emptyOrNullString())));
	}
	@Test
	public void shouldReturnInternalServerErrorAndSuccessFalse_whenTheServiceIsNullOnRetrieveItemsByIdInvocation() throws Exception {
		List<Integer> itemIdList = List.of(1,2,3);

		when(service.getItemsByIds(argThat(itemIds->itemIds.contains(1) &&
				itemIds.contains(2) &&
				itemIds.contains(3))))
				.thenThrow(NullPointerException.class);

		mockMvc.perform(post("/inventory/items")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(itemIdList))
				).andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message",not(emptyOrNullString())));
	}


	@Test
	public void shouldReturnAllTheItems_whenInventoryApiInvocation() throws Exception {
		ItemDTO sledgeHammer = getSledgeHammer();
		ItemDTO nailHammer = getNailHammer();
		ItemDTO screwDriver = getScrewDriver();

		when(service.getAllItems()).thenReturn(List.of(sledgeHammer,nailHammer,screwDriver));

		mockMvc.perform(get("/inventory"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message").value(""))
				.andExpect(jsonPath("$.data[0].name",not(emptyOrNullString())))
				.andExpect(jsonPath("$.data[1].name",not(emptyOrNullString())))
				.andExpect(jsonPath("$.data[1].name",not(emptyOrNullString())));
	}

	@Test
	public void shouldNotReturnAnyItem_whenInventoryIsEmptyOnInventoryApiInvocation() throws Exception{
		when(service.getAllItems()).thenReturn(Collections.emptyList());

		mockMvc.perform(get("/inventory"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.message",not(emptyOrNullString())))
				.andExpect(jsonPath("$.data",hasSize(0)));
	}
	
	@Test
	public void shouldReturnAllocatedItems_whenItemIdsAndQuantitiesPassedOnAllocateAPIInvocation() throws Exception{
		int orderLineNumberOfHammers = 2;
		int orderLineNumberOfScrewDrivers = 3;

		ItemDTO sledgeHammer = getSledgeHammer();
		sledgeHammer.setQuantity(String.valueOf(orderLineNumberOfHammers));
		
		ItemDTO screwDriver = getScrewDriver();
		screwDriver.setQuantity(String.valueOf(orderLineNumberOfScrewDrivers));

		ItemDTO orderLineSledgeHammer = getSledgeHammer();
		orderLineSledgeHammer.setName(null);
		orderLineSledgeHammer.setDescription(null);
		orderLineSledgeHammer.setPrice(null);
		orderLineSledgeHammer.setQuantity(String.valueOf(orderLineNumberOfHammers));

		ItemDTO orderLineScrewDriver = getScrewDriver();
		orderLineScrewDriver.setName(null);
		orderLineScrewDriver.setDescription(null);
		orderLineScrewDriver.setPrice(null);
		orderLineScrewDriver.setQuantity(String.valueOf(orderLineNumberOfScrewDrivers));
		
		List<ItemDTO> orderLines = List.of(orderLineScrewDriver,orderLineSledgeHammer);

		when(service.allocateInventory(any())).thenReturn(List.of(sledgeHammer,screwDriver));
		
		mockMvc.perform(post("/inventory/allocate")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(orderLines)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(true))
				.andExpect(jsonPath("$.data",hasSize(orderLines.size())));
	}

	@Test
	public void shouldReturnBadRequestAndSuccessFalse_whenNoItemIdsAndQuantitiesPassedOnAllocateAPIInvocation() throws Exception{
		List<ItemDTO> orderLines = Collections.emptyList();

		mockMvc.perform(post("/inventory/allocate")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(orderLines)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.data",hasSize(orderLines.size())));

	}

	@Test
	public void shouldReturnStatusOkAndSuccessFalse_whenItemIdsAreNotAvailableOnAllocateAPIInvocation() throws Exception{
		int orderLineNumberOfHammers = 2;
		int orderLineNumberOfScrewDrivers = 3;

		ItemDTO orderLineSledgeHammer = new ItemDTO();
		orderLineSledgeHammer.setQuantity(String.valueOf(orderLineNumberOfHammers));
		orderLineSledgeHammer.setId(4);
		

		ItemDTO orderLineScrewDriver = new ItemDTO();
		orderLineScrewDriver.setQuantity(String.valueOf(orderLineNumberOfScrewDrivers));
		orderLineScrewDriver.setId(5);

		List<ItemDTO> orderLines = List.of(orderLineScrewDriver,orderLineSledgeHammer);
		
		when(service.allocateInventory(any())).thenReturn(Collections.emptyList());

		mockMvc.perform(post("/inventory/allocate")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(orderLines)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.data",hasSize(0)));

	}

	@Test
	public void shouldReturnInternalServerErrorAndSuccessFalse_whenInventoryServiceIsNullOnAllocateAPIInvocation() throws Exception{
		int orderLineNumberOfHammers = 2;
		int orderLineNumberOfScrewDrivers = 3;

		ItemDTO orderLineSledgeHammer = new ItemDTO();
		orderLineSledgeHammer.setQuantity(String.valueOf(orderLineNumberOfHammers));
		orderLineSledgeHammer.setId(1);


		ItemDTO orderLineScrewDriver = new ItemDTO();
		orderLineScrewDriver.setQuantity(String.valueOf(orderLineNumberOfScrewDrivers));
		orderLineScrewDriver.setId(2);

		List<ItemDTO> orderLines = List.of(orderLineScrewDriver,orderLineSledgeHammer);

		when(service.allocateInventory(any())).thenThrow(NullPointerException.class);

		mockMvc.perform(post("/inventory/allocate")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(orderLines)))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.data",hasSize(0)));
	}

	@Test
	public void shouldReturnInternalServerErrorAndSuccessFalse_whenServiceIsNullOnInventoryApiInvocation() throws Exception{
		when(service.getAllItems()).thenThrow(NullPointerException.class);

		mockMvc.perform(get("/inventory"))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.success").value(false))
				.andExpect(jsonPath("$.message",not(emptyOrNullString())))
				.andExpect(jsonPath("$.data",hasSize(0)));

	}
	private ItemDTO getSledgeHammer(){
		ItemDTO sledgeHammer = new ItemDTO();
		sledgeHammer.setName(TestConstantValues.ITEM_SLEDGE_HAMMER_NAME);
		sledgeHammer.setDescription(TestConstantValues.ITEM_SLEDGE_HAMMER_DESCRIPTION);
		sledgeHammer.setPrice(TestConstantValues.ITEM_SLEDGE_HAMMER_PRICE);
		sledgeHammer.setQuantity(TestConstantValues.ITEM_SLEDGE_HAMMER_QUANTITY);
		return sledgeHammer;
	}
	
	private ItemDTO getNailHammer(){
		ItemDTO nailHammer = new ItemDTO();
		nailHammer.setName(TestConstantValues.ITEM_NAIL_HAMMER_NAME);
		nailHammer.setDescription(TestConstantValues.ITEM_NAIL_HAMMER_DESCRIPTION);
		nailHammer.setPrice(TestConstantValues.ITEM_NAIL_HAMMER_PRICE);
		nailHammer.setQuantity(TestConstantValues.ITEM_NAIL_HAMMER_QUANTITY);
		return nailHammer;
	}
	
	private ItemDTO getScrewDriver(){
		ItemDTO screwDriver = new ItemDTO();
		screwDriver.setName(TestConstantValues.ITEM_SCREW_DRIVE_NAME);
		screwDriver.setDescription(TestConstantValues.ITEM_SCREW_DRIVE_DESCRIPTION);
		screwDriver.setQuantity(TestConstantValues.ITEM_SCREW_DRIVE_QUANTITY);
		screwDriver.setPrice(TestConstantValues.ITEM_SCREW_DRIVE_PRICE);
		return screwDriver;
	}
}
