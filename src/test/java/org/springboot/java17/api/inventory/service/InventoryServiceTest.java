package org.springboot.java17.api.inventory.service;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springboot.java17.api.inventory.TestConstantValues;
import org.springboot.java17.api.common.dto.ItemDTO;
import org.springboot.java17.api.inventory.model.InventoryRepository;
import org.springboot.java17.api.inventory.model.Item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

	@Mock
	private InventoryRepository repository;

	@InjectMocks
	private InventoryServiceImpl service;

	@Test
	public void shouldReturnItemDetails_whenItemAddedSuccessfully() {
		ItemDTO addItem = new ItemDTO();
		addItem.setName(TestConstantValues.ITEM_SLEDGE_HAMMER_NAME);
		addItem.setDescription(TestConstantValues.ITEM_SLEDGE_HAMMER_DESCRIPTION);
		addItem.setPrice(TestConstantValues.ITEM_SLEDGE_HAMMER_PRICE);
		addItem.setQuantity(TestConstantValues.ITEM_SLEDGE_HAMMER_QUANTITY);

		Item createdItem = new Item();
		createdItem.setId(1);
		createdItem.setName(TestConstantValues.ITEM_SLEDGE_HAMMER_NAME);
		createdItem.setDescription(TestConstantValues.ITEM_SLEDGE_HAMMER_DESCRIPTION);
		createdItem.setPrice(new BigDecimal(TestConstantValues.ITEM_SLEDGE_HAMMER_PRICE));
		createdItem.setQuantity(Integer.parseInt(TestConstantValues.ITEM_SLEDGE_HAMMER_QUANTITY));


		when(repository.save(argThat(item -> TestConstantValues.ITEM_SLEDGE_HAMMER_NAME.equals(item.getName()) &&
				TestConstantValues.ITEM_SLEDGE_HAMMER_DESCRIPTION.equals(item.getDescription()) &&
				TestConstantValues.ITEM_SLEDGE_HAMMER_QUANTITY.equals(String.valueOf(item.getQuantity())) &&
				TestConstantValues.ITEM_SLEDGE_HAMMER_PRICE.equals(String.valueOf(item.getPrice())))))
				.thenReturn(createdItem);

		ItemDTO returnedValue = service.addItem(addItem);

		assertNotNull(returnedValue);
		assertEquals(TestConstantValues.ITEM_SLEDGE_HAMMER_NAME, returnedValue.getName());
		assertEquals(TestConstantValues.ITEM_SLEDGE_HAMMER_DESCRIPTION, returnedValue.getDescription());
		assertEquals(TestConstantValues.ITEM_SLEDGE_HAMMER_PRICE, returnedValue.getPrice());
		assertEquals(TestConstantValues.ITEM_SLEDGE_HAMMER_QUANTITY, returnedValue.getQuantity());

		Item savedItem = new Item();
		savedItem.setName(TestConstantValues.ITEM_SLEDGE_HAMMER_NAME);
		savedItem.setDescription(TestConstantValues.ITEM_SLEDGE_HAMMER_DESCRIPTION);
		savedItem.setPrice(new BigDecimal(TestConstantValues.ITEM_SLEDGE_HAMMER_PRICE));
		savedItem.setQuantity(Integer.parseInt(TestConstantValues.ITEM_SLEDGE_HAMMER_QUANTITY));
		savedItem.setId(1);
		verify(repository).save(
				argThat(item -> TestConstantValues.ITEM_SLEDGE_HAMMER_NAME.equals(item.getName()) && TestConstantValues.ITEM_SLEDGE_HAMMER_DESCRIPTION.equals(
						item.getDescription()) && new BigDecimal(TestConstantValues.ITEM_SLEDGE_HAMMER_PRICE).equals(
						item.getPrice()) && Integer.parseInt(TestConstantValues.ITEM_SLEDGE_HAMMER_QUANTITY) == item.getQuantity()));

	}

	@Test
	public void shouldReturnNUll_whenItemPassedIsNUll() {
		ItemDTO returnedValue = service.addItem(null);

		Assertions.assertNull(returnedValue);
	}
	
	@Test
	public void shouldReturnItemDetails_whenProvidePartOfItemName() throws Exception {
		Item sledgeHammer = new Item();
		sledgeHammer.setName(TestConstantValues.ITEM_SLEDGE_HAMMER_NAME);
		sledgeHammer.setDescription(TestConstantValues.ITEM_SLEDGE_HAMMER_DESCRIPTION);
		sledgeHammer.setPrice(new BigDecimal(TestConstantValues.ITEM_SLEDGE_HAMMER_PRICE));
		sledgeHammer.setQuantity(Integer.parseInt(TestConstantValues.ITEM_SLEDGE_HAMMER_QUANTITY));
		sledgeHammer.setId(1);
		
		Item nailHammer = new Item();
		nailHammer.setName(TestConstantValues.ITEM_NAIL_HAMMER_NAME);
		nailHammer.setDescription(TestConstantValues.ITEM_NAIL_HAMMER_DESCRIPTION);
		nailHammer.setPrice(new BigDecimal(TestConstantValues.ITEM_NAIL_HAMMER_PRICE));
		nailHammer.setQuantity(Integer.parseInt(TestConstantValues.ITEM_NAIL_HAMMER_QUANTITY));
		nailHammer.setId(2);
		
		final String searchTerm = "hammer";

		when(repository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(List.of(sledgeHammer, nailHammer));

		List<ItemDTO> items = service.getItemsByName(searchTerm);
		
		assertNotNull(items);
		assertThat(items).hasSize(2);
		assertThat(items.get(0).getName()).isEqualTo(TestConstantValues.ITEM_SLEDGE_HAMMER_NAME);
		assertThat(items.get(0).getDescription()).isEqualTo(TestConstantValues.ITEM_SLEDGE_HAMMER_DESCRIPTION);
		assertThat(items.get(0).getPrice()).isEqualTo(TestConstantValues.ITEM_SLEDGE_HAMMER_PRICE);
		assertThat(items.get(0).getQuantity()).isEqualTo(TestConstantValues.ITEM_SLEDGE_HAMMER_QUANTITY);
		
		assertThat(items.get(1).getName()).isEqualTo(TestConstantValues.ITEM_NAIL_HAMMER_NAME);
		assertThat(items.get(1).getDescription()).isEqualTo(TestConstantValues.ITEM_NAIL_HAMMER_DESCRIPTION);
		assertThat(items.get(1).getPrice()).isEqualTo(TestConstantValues.ITEM_NAIL_HAMMER_PRICE);
		assertThat(items.get(1).getQuantity()).isEqualTo(TestConstantValues.ITEM_NAIL_HAMMER_QUANTITY);
				
		verify(repository).findByNameContainingIgnoreCase(searchTerm);
	}
	
	@Test
	public void shouldReturnNull_whenItemsWithSearchTermIsNotFound() throws Exception{
		final String searchTerm = "aaa";
		
		when(repository.findByNameContainingIgnoreCase(searchTerm)).thenReturn(List.of());
		
		List<ItemDTO> items = service.getItemsByName(searchTerm);
		
		assertThat(items).isNull();
	}
	
	@Test
	public void shouldReturnNull_whenSearchTermIsEmpty() throws Exception{
		final String searchTerm = "";
		
		List<ItemDTO> items = service.getItemsByName(searchTerm);
		
		assertThat(items).isNull();
	}
}
