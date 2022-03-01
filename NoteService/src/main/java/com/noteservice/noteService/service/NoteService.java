package com.noteservice.noteService.service;

import java.util.List;

import com.noteservice.noteService.dto.NoteDTO;
import com.noteservice.noteService.entity.Note;

public interface NoteService {

	public  Note createNote(NoteDTO noteDTO, String token);
	
	public Note updateNote(String token,Long noteId, NoteDTO noteDTO);
	
	public Note pinNote(String token,Long noteId);
	
	public List<Note> getAllNotes(String token);
	
	public Note getNote(String token, Long noteId);
	
}
