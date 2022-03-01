package com.noteservice.noteService.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.noteservice.noteService.dto.NoteDTO;
import com.noteservice.noteService.entity.Note;
import com.noteservice.noteService.exception.FundooException;
import com.noteservice.noteService.response.Response;
import com.noteservice.noteService.service.NoteService;


@RestController
@RequestMapping("/note")
public class NoteController {
	
	@Value("${server.port}")
	private String port;
	
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private NoteService noteService;

	@PostMapping
	public ResponseEntity<Response> createNote(@Valid @RequestBody NoteDTO noteDTO, @RequestHeader String token,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors())
			throw new FundooException(HttpStatus.UNPROCESSABLE_ENTITY.value(),
					bindingResult.getAllErrors().get(0).getDefaultMessage());
		Note note = noteService.createNote(noteDTO, token);
		return new ResponseEntity<Response>(new Response(HttpStatus.CREATED.value(), "Note Created Successfully", note),
				HttpStatus.CREATED);
	}

	@PutMapping("/{noteId}")
	public ResponseEntity<Response> updateNote(@Valid @RequestBody NoteDTO noteDTO, @RequestHeader String token,
			@PathVariable Long noteId, BindingResult bindingResult) {
		if (bindingResult.hasErrors())
			throw new FundooException(HttpStatus.UNPROCESSABLE_ENTITY.value(),
					bindingResult.getAllErrors().get(0).getDefaultMessage());
		Note note = noteService.updateNote(token, noteId, noteDTO);
		return new ResponseEntity<Response>(new Response(HttpStatus.OK.value(), "Note Updated Successfully", note),
				HttpStatus.OK);
	}
	
	@PutMapping(value = {"/pin/{noteId}","/un-pin/{noteId}"})
	public ResponseEntity<Response> changePin( @RequestHeader String token,
			@PathVariable Long noteId) {
		Note note = noteService.pinNote(token, noteId);
		return new ResponseEntity<Response>(new Response(HttpStatus.OK.value(), "Note Updated Successfully", note),
				HttpStatus.OK);
	}
	
	@GetMapping()
	public ResponseEntity<Response> getAllNotes( @RequestHeader String token) {
		List<Note> notes = noteService.getAllNotes(token);
		return new ResponseEntity<Response>(new Response(HttpStatus.OK.value(), "Notes Retrived Successfully", notes),
				HttpStatus.OK);
	}
	
	@GetMapping(value = "/{noteId}")
	public ResponseEntity<Response> getAllNotes( @RequestHeader String token, @PathVariable Long noteId) {
		Note note = noteService.getNote(token, noteId);
		return new ResponseEntity<Response>(new Response(HttpStatus.OK.value(), "Note Retrived Successfully", note),
				HttpStatus.OK);
	}
	
	@GetMapping("/port")
	public String getPort(){
		 HttpHeaders headers = new HttpHeaders();
	      //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	      HttpEntity <String> entity = new HttpEntity<String>(headers);
	      
	      String port =  restTemplate.exchange("http://user-service/user/port", HttpMethod.GET, entity, String.class).getBody();
	      return port;
	      }
}
