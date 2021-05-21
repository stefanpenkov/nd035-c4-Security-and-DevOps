package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
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

public class CartControllerTest {
    private CartController cartController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setup() {
        cartController = new CartController();
        TestUtils.injectObject(cartController, "userRepository", userRepo);
        TestUtils.injectObject(cartController, "cartRepository", cartRepo);
        TestUtils.injectObject(cartController, "itemRepository", itemRepo);
    }

    @Test
    public void addToCart() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(24L);
        modifyCartRequest.setUsername("john");
        modifyCartRequest.setQuantity(1);

        User user = new User();
        user.setUsername("john");
        user.setPassword("john@doe");
        user.setId(5L);

        Item item = new Item();
        item.setId(24L);
        item.setName("someItem");
        item.setPrice(BigDecimal.ONE);
        item.setDescription("someDescription");
        List<Item> listItems = new ArrayList<>();
        listItems.add(item);

        Cart cart = new Cart();
        cart.setId(3L);
        cart.setItems(listItems);
        cart.setTotal(BigDecimal.ONE);
        cart.setUser(user);

        user.setCart(cart);

        when(userRepo.findByUsername("john")).thenReturn(user);
        when(itemRepo.findById(24L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart result = response.getBody();
        assert result != null;
        assertEquals(listItems, result.getItems());
        assertEquals(BigDecimal.valueOf(2), result.getTotal());
    }
    @Test
    public void removeToCart() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(24L);
        modifyCartRequest.setUsername("john");
        modifyCartRequest.setQuantity(1);

        User user = new User();
        user.setUsername("john");
        user.setPassword("john@doe");
        user.setId(5L);

        Item item = new Item();
        item.setId(24L);
        item.setName("someItem");
        item.setPrice(BigDecimal.ONE);
        item.setDescription("someDescription");
        List<Item> listItems = new ArrayList<>();
        listItems.add(item);

        Cart cart = new Cart();
        cart.setId(3L);
        cart.setItems(listItems);
        cart.setTotal(BigDecimal.ONE);
        cart.setUser(user);

        user.setCart(cart);

        when(userRepo.findByUsername("john")).thenReturn(user);
        when(itemRepo.findById(24L)).thenReturn(Optional.of(item));

        ResponseEntity<Cart> response = cartController.removeFromCart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart result = response.getBody();
        assert result != null;
        assertEquals(listItems, result.getItems());
        assertEquals(BigDecimal.valueOf(0), result.getTotal());
    }

}
