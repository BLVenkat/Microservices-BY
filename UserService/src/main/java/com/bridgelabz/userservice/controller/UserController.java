package com.bridgelabz.userservice.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bridgelabz.userservice.dto.LoginDTO;
import com.bridgelabz.userservice.dto.UserDTO;
import com.bridgelabz.userservice.entity.User;
import com.bridgelabz.userservice.exception.FundooException;
import com.bridgelabz.userservice.response.Response;
import com.bridgelabz.userservice.service.UserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/user")
@Api(description = "Controller for all user related operations")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Value("${server.port}")
	private String port;
	
	@PostMapping("/register")
	@ApiOperation("Api to register a user for fundonotes")
	public ResponseEntity<Response> register(@Valid @RequestBody UserDTO userDTO,BindingResult result){

		if(result.hasErrors()) {
			throw new FundooException(HttpStatus.UNPROCESSABLE_ENTITY.value(),result.getAllErrors().get(0).getDefaultMessage());
		}
		User user = userService.register(userDTO);
		return new ResponseEntity<Response>(new Response(HttpStatus.CREATED.value(), "User Registered Successfully", user), HttpStatus.CREATED);
	}
	
	@PostMapping("/login")
	@ApiOperation("Api to login a user for fundonotes")
	public ResponseEntity<Response> login(@RequestBody LoginDTO loginDTO){
		String token = userService.login(loginDTO);
		return new ResponseEntity<Response>(new Response(HttpStatus.OK.value(), "User Sign Successfully", token), HttpStatus.OK);
	}
	
	@GetMapping("/verify-email/{token}")
	@ApiOperation("Api to verify email of a user for fundonotes")
	public ResponseEntity<Response> verifyEmail(@PathVariable String token){
		User user = userService.verifyEmail(token);
		return new ResponseEntity<Response>(new Response(HttpStatus.OK.value(), "User Email Verified Successfully", user), HttpStatus.OK);
	}
	
	@GetMapping("/forgot-password")
	@ApiOperation("Api to send password reset link to mail")
	public ResponseEntity<Response> forgotPassword(@RequestParam String email){
		 userService.forgotPassword(email);
		return new ResponseEntity<Response>(new Response(HttpStatus.OK.value(), "Reset link has sent to mail Successfully", ""), HttpStatus.OK);
	}
	
	@GetMapping("/reset-password")
	@ApiOperation("Api to rest password of a user")
	public ResponseEntity<Response> resetPassword(@RequestHeader String token,  @RequestParam String password){
		User user =  userService.resetPassword(token, password);
		return new ResponseEntity<Response>(new Response(HttpStatus.OK.value(), "Password has been reset Successfully", user), HttpStatus.OK);
	}
	
	@GetMapping("")
	@ApiOperation("Api to get all users")
	public ResponseEntity<Response> getAllUser(){
		List<User> users =  userService.getAllUser();
		return new ResponseEntity<Response>(new Response(HttpStatus.OK.value(), "User Retrived Successfully", users), HttpStatus.OK);
	}
	
	@GetMapping("/{token}")
	public Long getUserId(@PathVariable String token){
		return userService.getUserId(token);
	}
	
	@GetMapping("/port")
	public String getPort(){
		return port;
	}
	
}
