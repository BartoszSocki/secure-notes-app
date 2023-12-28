package com.sockib.notesapp.service;

import com.sockib.notesapp.exception.WeakPasswordException;
import com.sockib.notesapp.model.dto.NoteFormDto;
import com.sockib.notesapp.model.entity.Note;

import java.util.List;
import java.util.Optional;

public interface NoteService {

    Note addNote(long userId, NoteFormDto noteFormDto) throws WeakPasswordException;
    List<Note> getNotesForUser(Long userId);
    Optional<Note> getNote(Long userId, Long noteId);

}
