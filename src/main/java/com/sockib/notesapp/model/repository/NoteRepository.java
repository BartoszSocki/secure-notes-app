package com.sockib.notesapp.model.repository;

import com.sockib.notesapp.model.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, String> {

    @Query("SELECT n FROM Note n WHERE n.user.id = :userId")
    List<Note> findUserNotes(Long userId);

    @Query("SELECT n FROM Note n WHERE n.user.id = :userId AND n.id = :noteId")
    Optional<Note> findUserNote(Long userId, Long noteId);

}