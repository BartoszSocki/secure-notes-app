package com.sockib.notesapp.controller;

import com.sockib.notesapp.exception.NoteException;
import com.sockib.notesapp.exception.WeakPasswordException;
import com.sockib.notesapp.model.dto.NoteDto;
import com.sockib.notesapp.model.dto.NoteFormDto;
import com.sockib.notesapp.model.entity.AppUser;
import com.sockib.notesapp.model.entity.Note;
import com.sockib.notesapp.service.NoteService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/note")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/add")
    String getAddNewNotePage(Model model) {
        if (model.containsAttribute("noteFormDto")) {
            model.addAttribute("noteFormDto", new NoteFormDto());
        }

        return "note-add";
    }

    @PostMapping("/add")
    String addNewNote(NoteFormDto noteFormDto,
                      RedirectAttributes redirectAttributes,
                      @AuthenticationPrincipal AppUser user) {
        try {
            Note note = noteService.addNote(user.getId(), noteFormDto);
        } catch (WeakPasswordException e) {
            noteFormDto.setEncryptionPassword("");
            redirectAttributes.addFlashAttribute("noteFormDto", noteFormDto);
            redirectAttributes.addFlashAttribute("error", e.getFailMessages());
            return "redirect:/dashboard/note/add";
        } catch (NoteException e) {
            noteFormDto.setEncryptionPassword("");
            noteFormDto.setIsEncrypted(false);
            noteFormDto.setIsPublished(false);
            redirectAttributes.addFlashAttribute("noteFormDto", noteFormDto);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        redirectAttributes.addFlashAttribute("success", "successfully added new note");
        return "redirect:/dashboard";
    }

    @GetMapping("/list")
    String noteList(Model model, @AuthenticationPrincipal AppUser user) {
        List<Note> notes = noteService.getNotesForUser(user.getId());

        model.addAttribute("notes", notes);
        return "note-list";
    }

    @GetMapping("/{noteId}")
    String notePage(@PathVariable Long noteId,
                    Model model,
                    RedirectAttributes redirectAttributes,
                    @AuthenticationPrincipal AppUser user) {
        Note note = noteService.getNote(noteId).orElseThrow();
        boolean isOwner = Objects.equals(user.getId(), note.getUser().getId());

        if (isOwner || note.getIsPublished()) {
            model.addAttribute("note", note);
            return "note";
        }

        if (isOwner && note.getIsEncrypted()) {
            redirectAttributes.addFlashAttribute("note", note);
            return String.format("redirect:/note/%d/password", noteId);
        }

        throw new AccessDeniedException(user.getUsername() + " dont have access to note with id " + noteId);
    }

    @GetMapping("/{noteId}/password")
    String encryptedNote(@PathVariable Long noteId,
                         Model model,
                         @AuthenticationPrincipal AppUser user) {
        Note note = noteService.getNote(noteId).orElseThrow();
        boolean isOwner = Objects.equals(user.getId(), note.getUser().getId());

        if (isOwner || note.getIsPublished()) {
            return String.format("redirect:/note/%d", noteId);
        }

        if (isOwner && note.getIsEncrypted()) {
            return "note-password";
        }

        throw new AccessDeniedException(user.getUsername() + " dont have access to note with id " + noteId);
    }

    @PostMapping("/{noteId}/password")
    String encryptedNote(@PathVariable Long noteId) {
        return "note";
    }

}
