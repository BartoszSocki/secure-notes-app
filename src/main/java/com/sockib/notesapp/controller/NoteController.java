package com.sockib.notesapp.controller;

import com.sockib.notesapp.model.dto.NoteFormDto;
import com.sockib.notesapp.model.entity.AppUser;
import com.sockib.notesapp.service.NoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/note")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/add")
    String getAddNewNotePage(Model model) {
        model.addAttribute("noteFormDto", new NoteFormDto());
        return "note-add";
    }

    @PostMapping("/add")
    String addNewNote(NoteFormDto noteFormDto, Principal principal, RedirectAttributes redirectAttributes) {
//        AppUser user = (AppUser) principal;

//        noteService.addNote(user.getId(), noteFormDto);

        redirectAttributes.addAttribute("success", "successfully added new note");
        return "redirect:/dashboard";
    }

}
