package com.sockib.notesapp.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteFormDto {

    private String title;
    private String content;
    private Boolean isPublished;
    private Boolean isEncrypted;
    private String encryptionPassword;

    public NoteFormDto() {
        this.title = "";
        this.content = "";
        this.isPublished = false;
        this.isEncrypted = false;
        this.encryptionPassword = "";
    }

}
