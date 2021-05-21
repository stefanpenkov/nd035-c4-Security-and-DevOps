package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setup() {
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepo);
    }

    @Test
    public void getItems() {
        Item item = new Item();
        item.setId(24L);
        item.setName("someItem");
        item.setPrice(BigDecimal.valueOf(27.0));
        item.setDescription("someDescription");

        List<Item> listItems = new ArrayList<>();
        listItems.add(item);
        when(itemRepo.findAll()).thenReturn(listItems);
        ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> returnedItems = response.getBody();
        assert returnedItems != null;
        Item returnedItem = returnedItems.get(0);

        assertEquals("someItem", returnedItem.getName());
        assertEquals("someDescription", returnedItem.getDescription());
    }
    @Test
    public void getItemById() {
        Long id = 24L;
        Item item = new Item();
        item.setId(id);
        item.setName("someItem");
        item.setPrice(BigDecimal.valueOf(27.0));
        item.setDescription("someDescription");

        when(itemRepo.findById(id)).thenReturn(Optional.of(item));
        ResponseEntity<Item> response = itemController.getItemById(id);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Item returnedItem = response.getBody();
        assert returnedItem != null;

        assertEquals("someItem", returnedItem.getName());
        assertEquals("someDescription", returnedItem.getDescription());
        assertEquals(id, returnedItem.getId());
    }

    @Test
    public void getItemsByName() {
        String name = "someItem";
        Item item = new Item();
        item.setId(24L);
        item.setName(name);
        item.setPrice(BigDecimal.valueOf(27.0));
        item.setDescription("someDescription");

        List<Item> listItems = new ArrayList<>();
        listItems.add(item);
        when(itemRepo.findByName(name)).thenReturn(listItems);
        ResponseEntity<List<Item>> response = itemController.getItemsByName(name);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> returnedItems = response.getBody();
        assert returnedItems != null;
        Item returnedItem = returnedItems.get(0);

        assertEquals("someItem", returnedItem.getName());
        assertEquals("someDescription", returnedItem.getDescription());
    }
}
