package com.sockib.notesapp.service.impl;

import com.sockib.notesapp.exception.WeakPasswordException;
import com.sockib.notesapp.model.dto.NoteFormDto;
import com.sockib.notesapp.model.embeddable.NoteContent;
import com.sockib.notesapp.model.entity.Note;
import com.sockib.notesapp.model.repository.NoteRepository;
import com.sockib.notesapp.model.repository.UserRepository;
import com.sockib.notesapp.policy.note.NoteContentPolicy;
import com.sockib.notesapp.policy.note.NoteTitlePolicy;
import com.sockib.notesapp.policy.note.Sanitizer;
import com.sockib.notesapp.policy.password.PasswordStrengthPolicy;
import com.sockib.notesapp.policy.password.PasswordStrengthResult;
import com.sockib.notesapp.service.NoteEncryptionService;
import com.sockib.notesapp.service.NoteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NoteServiceImpl implements NoteService {

    private final PasswordStrengthPolicy passwordStrengthPolicy;
    private final Sanitizer noteContentPolicy;
    private final Sanitizer noteTitlePolicy;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final NoteEncryptionService noteEncryptionService;

    public NoteServiceImpl(PasswordStrengthPolicy passwordStrengthPolicy,
                           NoteRepository noteRepository,
                           UserRepository userRepository,
                           NoteEncryptionService noteEncryptionService) {
        this.passwordStrengthPolicy = passwordStrengthPolicy;
        this.userRepository = userRepository;
        this.noteContentPolicy = new NoteContentPolicy();
        this.noteTitlePolicy = new NoteTitlePolicy();
        this.noteRepository = noteRepository;
        this.noteEncryptionService = noteEncryptionService;
    }

    @Override
    public Note addNote(long userId, NoteFormDto noteFormDto) throws WeakPasswordException {
        String sanitizedNoteContent = noteContentPolicy.sanitize(noteFormDto.getContent());
        String sanitizedTitle = noteTitlePolicy.sanitize(noteFormDto.getTitle());

        boolean isPublished = noteFormDto.getIsPublished();
        boolean isEncrypted = noteFormDto.getIsEncrypted();

        Note note = new Note();
        note.setTitle(sanitizedTitle);
        note.setIsEncrypted(isEncrypted);
        note.setIsPublished(isPublished);
        note.setUser(userRepository.getReferenceById(userId));

        if (isEncrypted) {
            PasswordStrengthResult passwordStrength = passwordStrengthPolicy.evaluate(noteFormDto.getEncryptionPassword());
            if (!passwordStrength.isStrong()) {
                throw new WeakPasswordException(passwordStrength.getFailMessages());
            }

            String encryptionPassword = noteFormDto.getEncryptionPassword();
            NoteContent noteContent = noteEncryptionService.encrypt(sanitizedNoteContent, encryptionPassword);

            note.setNoteContent(noteContent);
        } else {
            NoteContent noteContent = new NoteContent();
            noteContent.setContent(sanitizedNoteContent);

            note.setNoteContent(noteContent);
        }

        return noteRepository.save(note);
    }

    @Override
    public List<Note> getNotesForUser(Long userId) {
        return noteRepository.findUserNotes(userId);
    }

    @Override
    public Optional<Note> getNote(Long userId, Long noteId) {
        return noteRepository.findUserNote(userId, noteId);
    }

}
