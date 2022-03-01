package com.noteservice.noteService.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.noteservice.noteService.dto.NoteDTO;
import com.noteservice.noteService.entity.Note;
import com.noteservice.noteService.exception.FundooException;
import com.noteservice.noteService.repository.NoteRepository;
import com.noteservice.noteService.util.TokenService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class NoteServiceImp implements NoteService {

	@Autowired
	private TokenService tokenService;

	@Autowired
	private NoteRepository noteRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
	private static final String RESILIENCE4J_INSTANCE_NAME="test";

	@Override
	@Transactional
	@CircuitBreaker(name = RESILIENCE4J_INSTANCE_NAME,fallbackMethod = "fallback")
	public Note createNote(NoteDTO noteDTO, String token) {
		Long userId = getUser(token);
		Note note = new Note();
		BeanUtils.copyProperties(noteDTO, note);
		note.setUserId(userId);
		note = noteRepository.save(note);
		return note;
	}

	private Long decodeToken(String token) {
		return tokenService.decodeToken(token);
	}

	private Note getNote(Long userId, Long noteId) {
		return noteRepository.findByUserIdAndId(userId, noteId)
				.orElseThrow(() -> new FundooException(HttpStatus.NOT_FOUND.value(), "Note not Found"));

	}

	@Override
	public Note updateNote(String token, Long noteId, NoteDTO noteDTO) {
		Long userId = decodeToken(token);
		Note note = getNote(userId, noteId);
		BeanUtils.copyProperties(noteDTO, note);
		return noteRepository.save(note);
	}

	@Override
	public Note pinNote(String token, Long noteId) {
		Long userId = decodeToken(token);
		Note note = getNote(userId, noteId);
		if (note.getIsPinned() == true)
			note.setIsPinned(false);
		else
			note.setIsPinned(true);
		return noteRepository.save(note);
	}

	@Override
	public List<Note> getAllNotes(String token) {
		Long userId = decodeToken(token);
		return noteRepository.findByUserId(userId);
	}

	@Override
	public Note getNote(String token, Long noteId) {
		Long userId = decodeToken(token);
		return getNote(userId, noteId);
	}
	
	public Long getUser(String token) {
		 HttpHeaders headers = new HttpHeaders();
	      //headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	      HttpEntity <String> entity = new HttpEntity<String>(headers);
	      
	      Long userId =  restTemplate.exchange("http://user-service/user/"+token, HttpMethod.GET, entity, Long.class).getBody();
	      return userId;
	}
	
	public Long fallback() {
		return null;
		//throw new FundooException(HttpStatus.BAD_REQUEST.value(), "User Service is dowm");
	}

}
