package com.sockib.notesapp.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteDto {

    private long id;
    private String title;
    private String content;

}
