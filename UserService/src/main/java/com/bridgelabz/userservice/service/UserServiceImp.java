package com.bridgelabz.userservice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bridgelabz.userservice.dto.LoginDTO;
import com.bridgelabz.userservice.dto.UserDTO;
import com.bridgelabz.userservice.entity.User;
import com.bridgelabz.userservice.exception.FundooException;
import com.bridgelabz.userservice.repository.UserRepository;
import com.bridgelabz.userservice.util.TokenService;


@Service
public class UserServiceImp implements UserService{

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TokenService tokenService;
	
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public User register(UserDTO userDTO) {
		Optional<User> isUserPresent = userRepository.findByEmail(userDTO.getEmail());
		if (isUserPresent.isPresent()) {
			throw new FundooException(HttpStatus.CONFLICT.value(), "Email is already registered");
		}
		User user = new User();
		BeanUtils.copyProperties(userDTO, user);
		user.setIsEmailVerified(false);
		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		User savedUser = userRepository.save(user);
		return savedUser;
	}

	@Override
	public String login(LoginDTO loginDTO) {
		User user = userRepository.findByEmail(loginDTO.getEmail())
				.orElseThrow(() -> new FundooException(HttpStatus.NOT_FOUND.value(), "Email is not Registered"));
		if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword()))
			throw new FundooException(HttpStatus.UNAUTHORIZED.value(), "password is incorrect");
		return tokenService.createToken(user.getId());
	}

	@Override
	public User verifyEmail(String token) {
		Long userId = tokenService.decodeToken(token);
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new FundooException(HttpStatus.NOT_FOUND.value(), "User Not Found"));
		user.setIsEmailVerified(true);
		return userRepository.save(user);
	}

	@Override
	public void forgotPassword(String emailId) {
		User user = userRepository.findByEmail(emailId)
				.orElseThrow(() -> new FundooException(HttpStatus.NOT_FOUND.value(), "User Not Found"));
		String token = tokenService.createToken(user.getId());
	}

	@Override
	public User resetPassword(String token, String password) {
		Long userId = tokenService.decodeToken(token);
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new FundooException(HttpStatus.NOT_FOUND.value(), "User Not Found"));
		user.setPassword(passwordEncoder.encode(password));
		return userRepository.save(user);
	}

	@Override
	public List<User> getAllUser() {
		return userRepository.findAll();
	}

	@Override
	public Long getUserId(String token) {
		Long userId = tokenService.decodeToken(token);
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new FundooException(HttpStatus.NOT_FOUND.value(), "User Not Found"));
		return user.getId();
	}
}
