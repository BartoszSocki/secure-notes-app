package com.sockib.notesapp.controller;

import com.sockib.notesapp.exception.InvalidPasswordException;
import com.sockib.notesapp.exception.NoteException;
import com.sockib.notesapp.exception.ResourceNotFoundException;
import com.sockib.notesapp.exception.WeakPasswordException;
import com.sockib.notesapp.model.dto.NoteDto;
import com.sockib.notesapp.model.dto.NoteFormDto;
import com.sockib.notesapp.model.dto.NotePasswordFormDto;
import com.sockib.notesapp.model.entity.AppUser;
import com.sockib.notesapp.model.entity.Note;
import com.sockib.notesapp.service.NoteEncryptionService;
import com.sockib.notesapp.service.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
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
    private final NoteEncryptionService noteEncryptionService;

    public NoteController(NoteService noteService, NoteEncryptionService noteEncryptionService) {
        this.noteService = noteService;
        this.noteEncryptionService = noteEncryptionService;
    }

    @GetMapping("/add")
    String getAddNewNotePage(Model model) {
        if (!model.containsAttribute("noteFormDto")) {
            model.addAttribute("noteFormDto", new NoteFormDto());
        }

        return "note-add";
    }

    @PostMapping("/add")
    String addNewNote(NoteFormDto noteFormDto,
                      RedirectAttributes redirectAttributes,
                      @AuthenticationPrincipal AppUser user) {
        try {
            noteService.addNote(user.getId(), noteFormDto);
        } catch (WeakPasswordException e) {
            noteFormDto.setEncryptionPassword("");
            noteFormDto.setIsEncrypted(false);
            noteFormDto.setIsPublished(false);

            redirectAttributes.addAttribute("error", true);
            redirectAttributes.addAttribute("message", List.of(e.getMessage()));
            redirectAttributes.addFlashAttribute("noteFormDto", noteFormDto);
            redirectAttributes.addFlashAttribute("error", e.getFailMessages());
            return "redirect:/note/add";
        } catch (NoteException e) {
            noteFormDto.setEncryptionPassword("");
            noteFormDto.setIsEncrypted(false);
            noteFormDto.setIsPublished(false);

            redirectAttributes.addAttribute("error", true);
            redirectAttributes.addAttribute("message", List.of(e.getMessage()));
            redirectAttributes.addFlashAttribute("noteFormDto", noteFormDto);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/note/add";
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

    @GetMapping("/{noteId}/view")
    String noteViewPage(@PathVariable Long noteId,
                        Model model) {
        if (!model.containsAttribute("noteDto")) {
            return String.format("redirect:/note/%d", noteId);
        }

        return "note";
    }

    @GetMapping("/{noteId}")
    String notePage(@PathVariable Long noteId,
                    RedirectAttributes redirectAttributes,
                    @AuthenticationPrincipal AppUser user) throws ResourceNotFoundException {
        Note note = noteService.getNote(noteId).orElseThrow(ResourceNotFoundException::new);
        boolean isOwner = Objects.equals(user.getId(), note.getUser().getId());
        boolean isOwnerAndNotEncrypted = isOwner && !note.getIsEncrypted();
        boolean isPublished = note.getIsPublished();

        if (isOwnerAndNotEncrypted || isPublished) {
            NoteDto noteDto = new NoteDto();
            noteDto.setId(note.getId());
            noteDto.setTitle(note.getTitle());
            noteDto.setContent(note.getNoteContent().getContent());

            redirectAttributes.addFlashAttribute("noteDto", noteDto);
            return String.format("redirect:/note/%d/view", noteId);
        }

        if (isOwner) {
            redirectAttributes.addFlashAttribute("note", note);
            return String.format("redirect:/note/%d/password", noteId);
        }

        throw new AccessDeniedException(user.getUsername() + " dont have access to note with id " + noteId);
    }

    @GetMapping("/{noteId}/password")
    String encryptedNote(@PathVariable Long noteId,
                         Model model) {
        model.addAttribute("notePasswordFormDto", new NotePasswordFormDto());
        return "note-password";
    }

    @PostMapping("/{noteId}/password")
    String encryptedNote(@PathVariable Long noteId,
                         @AuthenticationPrincipal AppUser user,
                         NotePasswordFormDto notePasswordFormDto,
                         RedirectAttributes redirectAttributes) throws ResourceNotFoundException {
        Note note = noteService.getNote(noteId).orElseThrow(ResourceNotFoundException::new);
        boolean isOwner = Objects.equals(user.getId(), note.getUser().getId());

        if (!note.getIsEncrypted()) {
            return String.format("redirect:/note/%d", noteId);
        }

        if (!isOwner) {
            throw new AccessDeniedException(user.getUsername() + " dont have access to note with id " + noteId);
        }

        try {
            String content = noteEncryptionService.decrypt(note.getNoteContent(), notePasswordFormDto.getPassword());

            NoteDto noteDto = new NoteDto();
            noteDto.setId(note.getId());
            noteDto.setContent(content);
            noteDto.setTitle(note.getTitle());

            redirectAttributes.addFlashAttribute("noteDto", noteDto);
            return String.format("redirect:/note/%d/view", noteId);

        } catch (InvalidPasswordException e) {
            redirectAttributes.addAttribute("error", true);
            redirectAttributes.addAttribute("message", "Invalid password");
            return String.format("redirect:/note/%d/password", noteId);
        }
    }

}
