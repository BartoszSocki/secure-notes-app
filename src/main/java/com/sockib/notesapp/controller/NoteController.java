package com.sockib.notesapp.controller;

import com.sockib.notesapp.exception.WeakPasswordException;
import com.sockib.notesapp.model.dto.NoteDto;
import com.sockib.notesapp.model.dto.NoteFormDto;
import com.sockib.notesapp.model.entity.AppUser;
import com.sockib.notesapp.model.entity.Note;
import com.sockib.notesapp.service.NoteService;
import org.springframework.security.access.AccessDeniedException;
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
            System.out.println(note);
        } catch (WeakPasswordException e) {
            noteFormDto.setEncryptionPassword("");
            redirectAttributes.addFlashAttribute("noteFormDto", noteFormDto);
            redirectAttributes.addFlashAttribute("error", e.getFailMessages());
            return "redirect:/dashboard/note/add";
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
    String note(@PathVariable Long noteId,
                Model model,
                RedirectAttributes redirectAttributes,
                @AuthenticationPrincipal AppUser appUser) {
        Note note = noteService.getNote(appUser.getId(), noteId).orElseThrow(() -> new AccessDeniedException("Access Denied For Note: " + noteId));

        if (note.getIsEncrypted()) {
            return "redirect:/note/" + noteId + "/password";
        }

        NoteDto noteDto = new NoteDto();
        noteDto.setTitle(note.getTitle());
        noteDto.setContent(note.getNoteContent().getContent());

        model.addAttribute("noteDto", noteDto);
        return "note";
    }

    @GetMapping("/{noteId}/password")
    String encryptedNote(@PathVariable Long noteId, Model model) {
        return "note-password";
    }

    @PostMapping("/{noteId}/password")
    String encryptedNote(@PathVariable Long noteId) {
        return "redirect:/note/" + noteId;
    }

}
