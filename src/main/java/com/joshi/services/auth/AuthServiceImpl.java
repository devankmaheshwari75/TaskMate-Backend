package com.joshi.services.auth;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.joshi.dto.SignupRequest;
import com.joshi.dto.UserDto;
import com.joshi.entities.User;
import com.joshi.enums.UserRole;
import com.joshi.repositories.UserRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{
	
	private final UserRepository userRepository;
	
	@PostConstruct
	public void createAnAdminAccount() {
		Optional<User> optionalUser = userRepository.findByUserRole(UserRole.ADMIN);
		if(optionalUser.isEmpty()) {
			
			User user = new User();
			user.setEmail("admin@test.com");
			user.setName("admin");
			user.setPassword(new BCryptPasswordEncoder().encode("admin"));
			user.setUserRole(UserRole.ADMIN);
			userRepository.save(user);
			System.out.println("Admin Account Created Successfully!");
			
		}
		else {
			System.out.println("Admin Account Already exist");
		}
	}

	@Override
	public UserDto signupUser(SignupRequest signupRequest) {
		User user = new User();
		user.setEmail(signupRequest.getEmail());
		user.setName(signupRequest.getName());
		user.setPassword(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));
		user.setUserRole(UserRole.EMPLOYEE);
	    User createdUser = userRepository.save(user);
		return createdUser.getUserDto();
	}

	@Override
	public boolean hasUserWithEmail(String email) {
		// TODO Auto-generated method stub
		return userRepository.findFirstByEmail(email).isPresent();
	}
}
