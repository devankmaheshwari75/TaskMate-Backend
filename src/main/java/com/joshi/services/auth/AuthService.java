package com.joshi.services.auth;

import com.joshi.dto.SignupRequest;
import com.joshi.dto.UserDto;

public interface AuthService {

	UserDto signupUser(SignupRequest signupRequest);
	
	boolean hasUserWithEmail(String email);
}
