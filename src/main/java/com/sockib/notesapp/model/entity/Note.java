package com.sockib.notesapp.model.entity;

import com.sockib.notesapp.model.embeddable.NoteContent;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String title;
    private NoteContent noteContent;
    private Boolean isEncrypted = false;
    private Boolean isPublished = false;

}
