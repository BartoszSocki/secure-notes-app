package com.sockib.notesapp.model.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, String> {
}