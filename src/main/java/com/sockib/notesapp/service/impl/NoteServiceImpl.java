package com.sockib.notesapp.service.impl;

import com.sockib.notesapp.exception.NoteException;
import com.sockib.notesapp.exception.WeakPasswordException;
import com.sockib.notesapp.model.dto.NoteFormDto;
import com.sockib.notesapp.model.embeddable.NoteContent;
import com.sockib.notesapp.model.entity.Note;
import com.sockib.notesapp.model.repository.NoteRepository;
import com.sockib.notesapp.model.repository.UserRepository;
import com.sockib.notesapp.policy.Validator;
import com.sockib.notesapp.policy.note.NoteContentSanitizer;
import com.sockib.notesapp.policy.note.NoteContentValidator;
import com.sockib.notesapp.policy.note.NoteTitleSanitizer;
import com.sockib.notesapp.policy.Sanitizer;
import com.sockib.notesapp.policy.note.NoteTitleValidator;
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
    private final Sanitizer noteContentSanitizer;
    private final Sanitizer noteTitleSanitizer;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final NoteEncryptionService noteEncryptionService;
    private final Validator<String> noteContentValidator;
    private final Validator<String> noteTitleValidator;

    public NoteServiceImpl(PasswordStrengthPolicy passwordStrengthPolicy,
                           NoteRepository noteRepository,
                           UserRepository userRepository,
                           NoteEncryptionService noteEncryptionService) {
        this.passwordStrengthPolicy = passwordStrengthPolicy;
        this.userRepository = userRepository;
        this.noteContentSanitizer = new NoteContentSanitizer();
        this.noteTitleSanitizer = new NoteTitleSanitizer();
        this.noteTitleValidator = new NoteTitleValidator();
        this.noteContentValidator = new NoteContentValidator();
        this.noteRepository = noteRepository;
        this.noteEncryptionService = noteEncryptionService;
    }

    @Override
    public Note addNote(long userId, NoteFormDto noteFormDto) throws WeakPasswordException, NoteException {
        String sanitizedNoteContent = noteContentSanitizer.sanitize(noteFormDto.getContent());
        String sanitizedTitle = noteTitleSanitizer.sanitize(noteFormDto.getTitle());

        boolean isPublished = noteFormDto.getIsPublished();
        boolean isEncrypted = noteFormDto.getIsEncrypted();

        if (isPublished && isEncrypted) {
            throw new NoteException("invalid note state");
        }

        if (!noteTitleValidator.isValid(sanitizedNoteContent)) {
            throw new NoteException("title too long");
        }

        if (!noteContentValidator.isValid(sanitizedNoteContent)) {
            throw new NoteException("content too long");
        }

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
    public Optional<Note> getNote(Long noteId) {
        return noteRepository.findById(noteId);
    }

}
