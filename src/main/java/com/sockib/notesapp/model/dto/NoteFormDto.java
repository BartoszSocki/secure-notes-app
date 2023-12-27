package com.sockib.notesapp.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteFormDto {

    private String content;
    private Boolean isPublished;

}
