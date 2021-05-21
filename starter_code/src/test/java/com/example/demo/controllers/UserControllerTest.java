package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.security.UserDetailsServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    private UserDetailsServiceImpl userDetailsService = mock(UserDetailsServiceImpl.class);

    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObject(userController,"userRepository", userRepo);
        TestUtils.injectObject(userController, "cartRepository", cartRepo);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", encoder);
        TestUtils.injectObject(userController, "userDetailsService", userDetailsService);

    }

    @Test
    public void createUser()throws Exception{
        // replace the value of password
        when(encoder.encode("john@doe")).thenReturn("thisIsHashed");

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("john");
        request.setPassword("john@doe");
        request.setConfirmPassword("john@doe");
        final ResponseEntity<User> response = userController.createUser(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0,user.getId());
        assertEquals("john", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());

    }

    @Test
    public void signUpVerification() {
        // test if user with password less than 7 can be created
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("john");
        request.setPassword("john@");
        request.setConfirmPassword("john@");
        ResponseEntity<User> response = userController.createUser(request);

        assertEquals(400, response.getStatusCodeValue());

        //test if user with unmatched password can be created
        request.setUsername("john");
        request.setPassword("john@doe");
        request.setConfirmPassword("john@");
        response = userController.createUser(request);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void findById() {
        Long id = 18L;
        User user = new User();
        user.setId(id);
        user.setUsername("john");
        user.setPassword("john@doe");

        when(userRepo.findById(id)).thenReturn(Optional.of(user));
        ResponseEntity<User> response = userController.findById(id);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User returnedUser = response.getBody();
        assert returnedUser != null;
        assertEquals("john", returnedUser.getUsername());
        assertEquals("john@doe", returnedUser.getPassword());
    }

    @Test
    public void findByUsername() {
        String username = "john";
        User user = new User();
        user.setId(26L);
        user.setUsername(username);
        user.setPassword("john@doe");

        when(userRepo.findByUsername(username)).thenReturn(user);
        ResponseEntity<User> response = userController.findByUserName(username);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User returnedUser = response.getBody();
        assert returnedUser != null;
        assertEquals("john", returnedUser.getUsername());
        assertEquals("john@doe", returnedUser.getPassword());
    }

}
