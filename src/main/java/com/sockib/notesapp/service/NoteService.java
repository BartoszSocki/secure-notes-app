package com.sockib.notesapp.service;

import com.sockib.notesapp.exception.NoteException;
import com.sockib.notesapp.model.dto.NoteFormDto;
import com.sockib.notesapp.model.entity.Note;

public interface NoteService {

    Note addNote(long userId, NoteFormDto noteFormDto) throws NoteException;
    void encryptNote(long noteId, String password);
    void publishNote(long noteId);

}
