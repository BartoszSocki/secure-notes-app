package com.sockib.notesapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    String getDashboardPage() {
        return "dashboard";
    }

}
