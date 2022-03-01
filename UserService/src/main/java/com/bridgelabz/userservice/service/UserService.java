package com.bridgelabz.userservice.service;

import java.util.List;

import com.bridgelabz.userservice.dto.LoginDTO;
import com.bridgelabz.userservice.dto.UserDTO;
import com.bridgelabz.userservice.entity.User;

public interface UserService {

public User register(UserDTO userDTO);
	
	public String login(LoginDTO loginDTO);
	
	public User verifyEmail(String token);
	
	public void forgotPassword(String emailId);
	
	public User resetPassword(String token, String password);
	
	public List<User> getAllUser(); 
	
	public Long getUserId(String token);
	
}
