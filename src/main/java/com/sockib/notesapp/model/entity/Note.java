package com.sockib.notesapp.model.entity;

import com.sockib.notesapp.model.embeddable.NoteContent;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "note")
@Entity
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="owner_id", nullable=false)
    private AppUser user;

    private String title;
    private NoteContent noteContent;
    private Boolean isEncrypted = false;
    private Boolean isPublished = false;

}
