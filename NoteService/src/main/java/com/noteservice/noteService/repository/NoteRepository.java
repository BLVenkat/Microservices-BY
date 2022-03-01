package com.noteservice.noteService.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.noteservice.noteService.entity.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
	Optional<Note> findByUserIdAndId(Long userId,Long noteId);
	List<Note> findByUserId(Long userId);
}
