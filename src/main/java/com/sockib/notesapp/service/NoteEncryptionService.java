package com.sockib.notesapp.service;

import com.sockib.notesapp.exception.InvalidPasswordException;
import com.sockib.notesapp.model.embeddable.NoteContent;
import lombok.Getter;

public interface NoteEncryptionService {

    NoteContent encrypt(String text, String password);
    String decrypt(NoteContent encryptedText, String password) throws InvalidPasswordException;

}
