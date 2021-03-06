package com.noteservice.noteService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.noteservice.noteService.response.Response;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(FundooException.class)
	public ResponseEntity<Response> handleFundooException(FundooException ex){
		Response response = new Response(ex.getStatusCode(), ex.getStatusMessage(), null);
		return new ResponseEntity<Response>(response, HttpStatus.BAD_REQUEST);
	}
}
