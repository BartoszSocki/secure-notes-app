package com.sockib.notesapp.controller;

import com.sockib.notesapp.model.dto.LoginFormDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    String getLoginPage(Model model) {
        model.addAttribute("loginFormDto", new LoginFormDto());

        return "login";
    }

}
