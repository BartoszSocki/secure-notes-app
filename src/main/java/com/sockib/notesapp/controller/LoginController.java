package com.sockib.notesapp.controller;

import com.sockib.notesapp.model.dto.LoginFormDto;
import com.sockib.notesapp.model.dto.UserRegistrationDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @GetMapping("/login")
    String getLoginPage(Model model) {
        model.addAttribute("loginFormDto", new LoginFormDto());

        return "login";
    }

}
