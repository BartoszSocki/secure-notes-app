package com.sockib.notesapp.service.impl;

import com.sockib.notesapp.model.dto.NoteFormDto;
import com.sockib.notesapp.model.entity.Note;
import com.sockib.notesapp.policy.note.NoteContentPolicy;
import com.sockib.notesapp.service.NoteService;
import org.springframework.stereotype.Service;

@Service
public class NoteServiceImpl implements NoteService {

    private final NoteContentPolicy noteContentPolicy;

    public NoteServiceImpl() {
        this.noteContentPolicy = new NoteContentPolicy();
    }

    @Override
    public Note addNote(long userId, NoteFormDto noteFormDto) {
        String sanitizedNoteContent = noteContentPolicy.sanitize(noteFormDto.getContent());
        boolean isPublished = noteFormDto.getIsPublished();

        return null;
    }

    @Override
    public void encryptNote(long noteId, String password) {

    }

    @Override
    public void publishNote(long noteId) {

    }

}
