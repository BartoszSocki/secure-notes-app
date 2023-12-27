package com.sockib.notesapp.model.embeddable;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class NoteContent {

    private String content;
    private String encodedPassword;
    private byte[] salt;
    private byte[] iv;

    public NoteContent() {
    }
    public NoteContent(String content, String encodedPassword, byte[] salt, byte[] iv) {
        this.content = content;
        this.encodedPassword = encodedPassword;
        this.salt = salt;
        this.iv = iv;
    }

}
