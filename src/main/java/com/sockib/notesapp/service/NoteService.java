package com.sockib.notesapp.service;

import com.sockib.notesapp.exception.WeakPasswordException;
import com.sockib.notesapp.model.dto.NoteFormDto;
import com.sockib.notesapp.model.entity.Note;

public interface NoteService {

    Note addNote(long userId, NoteFormDto noteFormDto) throws WeakPasswordException;

}
