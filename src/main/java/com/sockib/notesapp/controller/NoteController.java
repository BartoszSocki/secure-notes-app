package com.sockib.notesapp.controller;

import com.sockib.notesapp.exception.NoteException;
import com.sockib.notesapp.exception.WeakPasswordException;
import com.sockib.notesapp.model.dto.NoteFormDto;
import com.sockib.notesapp.model.entity.AppUser;
import com.sockib.notesapp.service.NoteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/dashboard/note")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/add")
    String getAddNewNotePage(Model model) {
        if (model.getAttribute("noteFormDto") == null) {
            model.addAttribute("noteFormDto", new NoteFormDto());
        }

        return "note-add";
    }

    @PostMapping("/add")
    String addNewNote(NoteFormDto noteFormDto,
                      RedirectAttributes redirectAttributes,
                      HttpSession session) {
        AppUser user = (AppUser) session.getAttribute("user");

        try {
            noteService.addNote(user.getId(), noteFormDto);
        } catch (WeakPasswordException e) {
            noteFormDto.setEncryptionPassword("");
            redirectAttributes.addAttribute("noteFormDto", noteFormDto);
            redirectAttributes.addAttribute("error", e.getFailMessages());
            return "redirect:/dashboard/note/add";
        }

        redirectAttributes.addAttribute("success", "successfully added new note");
        return "redirect:/dashboard";
    }

}
