package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class OrderControllerTest {
    private OrderController orderController;
    private OrderRepository orderRepo = mock(OrderRepository.class);
    private UserRepository userRepo = mock(UserRepository.class);

    @Before
    public void setUp(){
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "orderRepository", orderRepo);
        TestUtils.injectObject(orderController, "userRepository", userRepo);

        User user = new User();
        user.setId(5L);
        user.setUsername("john");
        user.setPassword("john@doe");
        when(userRepo.findByUsername("john")).thenReturn(user);

        Item item = new Item();
        item.setId(12L);
        item.setPrice(BigDecimal.valueOf(27.0));
        item.setName("someItem");
        item.setDescription("someDescription");
        List<Item> listItems = new ArrayList<Item>();
        listItems.add(item);

        Cart cart = new Cart();
        cart.setId(3L);
        cart.setItems(listItems);
        cart.setUser(user);
        cart.setTotal(BigDecimal.valueOf(27.0));
        user.setCart(cart);

        UserOrder order = new UserOrder();
        order.setId(4L);
        order.setUser(user);
        order.setItems(listItems);
        order.setTotal(BigDecimal.ONE);
        List<UserOrder> listOrders = new ArrayList<>();
        listOrders.add(order);
        when(orderRepo.findByUser(user)).thenReturn(listOrders);
    }

    @Test
    public void submitOrder(){
        ResponseEntity<UserOrder> response = orderController.submit("john");
        assertEquals(BigDecimal.valueOf(27.0), Objects.requireNonNull(response.getBody()).getTotal());
        assertEquals(1, response.getBody().getItems().size());
        assertNull(response.getBody().getId());
    }

    @Test
    public void submitOrderForNonUser(){
        ResponseEntity<UserOrder> response = orderController.submit("nonUser");
        assertEquals("404 NOT_FOUND", response.getStatusCode().toString());
    }

    @Test
    public void getOrdersForUser() {

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("john");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = userRepo.findByUsername("john");
        List<UserOrder> result = response.getBody();

        assert result != null;
        UserOrder resultOrder = result.get(0);
        assertEquals(user, resultOrder.getUser());
    }
}
