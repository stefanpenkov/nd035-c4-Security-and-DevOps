package com.example.demo.controllers;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	private static final Pattern hasSpecialChar = Pattern.compile("[@#$%^&*_+=()]");

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		log.info("Searched username: " + username);

		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {

		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);

		String password = createUserRequest.getPassword();
		if(userRepository.findByUsername(createUserRequest.getUsername()) != null){
			log.error("Create user error: The username already exists.");
			return ResponseEntity.badRequest().build();

		}  if(password == null){
			log.error("Create user error: Please, provide password and confirm password for user {}",
					createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();

		}  if(password.length()<7){
			log.error("Create user error: Password must be at least 7 characters for user {}",
					createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();

		}  if(!hasSpecialChar.matcher(password).find()){
			log.error("Create user error: Password must have at least one special character [@#$%^&*_+=()] for user {}",
					createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();

		} if (!password.equals(createUserRequest.getConfirmPassword())){
			log.error("Create user error: The password and confirm password fields do not match for user {}",
					createUserRequest.getUsername());
			return ResponseEntity.badRequest().build();
		}

		String encryptedPassword = bCryptPasswordEncoder.encode(createUserRequest.getPassword());
		user.setPassword(encryptedPassword);
		log.info("Create user successfully. Username create: " + createUserRequest.getUsername()
				+ ", User password set: " + encryptedPassword);

		userRepository.save(user);
		return ResponseEntity.ok(user);
	}
	
}
