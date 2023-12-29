package com.sockib.notesapp.service.impl;

import com.sockib.notesapp.exception.NoteException;
import com.sockib.notesapp.exception.WeakPasswordException;
import com.sockib.notesapp.model.dto.NoteDto;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
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
    public void addNote(long userId, NoteFormDto noteFormDto) throws WeakPasswordException, NoteException {
        String sanitizedNoteContent = noteContentSanitizer.sanitize(noteFormDto.getContent());
        String sanitizedTitle = noteTitleSanitizer.sanitize(noteFormDto.getTitle());

        boolean isPublished = noteFormDto.getIsPublished();
        boolean isEncrypted = noteFormDto.getIsEncrypted();

        if (isPublished && isEncrypted) {
            log.error(String.format("error adding user (%d) note (%s), note is published and encrypted", userId, noteFormDto.getTitle()));
            throw new NoteException("invalid note state");
        }

        if (!noteTitleValidator.isValid(sanitizedTitle)) {
            log.error(String.format("error adding user (%d) note, note has too long title", userId));
            throw new NoteException("title too long");
        }

        if (!noteContentValidator.isValid(sanitizedNoteContent)) {
            log.error(String.format("error adding user (%d) note, note has too long content", userId));
            throw new NoteException("content too long");
        }

        Note note = new Note();
        note.setTitle(sanitizedTitle);
        note.setIsEncrypted(isEncrypted);
        note.setIsPublished(isPublished);
        note.setUser(userRepository.getReferenceById(userId));

        if (isEncrypted) {
            log.info(String.format("encrypting user (%d) note", userId));
            PasswordStrengthResult passwordStrength = passwordStrengthPolicy.evaluate(noteFormDto.getEncryptionPassword());
            if (!passwordStrength.isStrong()) {
                log.error(String.format("error encrypting user (%d) note, password too weak", userId));
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

        Note savedNote = noteRepository.save(note);
        log.info(String.format("successfully added user (%d) note (%d)", userId, savedNote.getId()));
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
