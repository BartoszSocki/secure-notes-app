package com.sockib.notesapp.controller;

import com.sockib.notesapp.exception.*;
import com.sockib.notesapp.model.dto.TotpCodeDto;
import com.sockib.notesapp.model.dto.UserRegistrationDto;
import com.sockib.notesapp.model.entity.AppUser;
import com.sockib.notesapp.service.RegistrationService;
import com.sockib.notesapp.service.impl.TotpServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    final RegistrationService registrationService;
    final TotpServiceImpl totpService;

    public RegistrationController(RegistrationService registrationService, TotpServiceImpl totpService) {
        this.registrationService = registrationService;
        this.totpService = totpService;
    }

    @GetMapping("/register")
    String registrationPage(Model model) {
        model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    String registerNewUser(UserRegistrationDto userRegistrationDto,
                           RedirectAttributes redirectAttributes,
                           HttpServletRequest request,
                           Model model) {
        try {
            AppUser appUser = registrationService.registerNewUser(userRegistrationDto);
            request.getSession().setAttribute("user", appUser);

            return "redirect:/register-totp";
        } catch (PasswordMismatchException e) {
            redirectAttributes.addAttribute("error", "passwords_mismatch");
            return "redirect:/register";
        } catch (UserAlreadyExistsException e) {
            redirectAttributes.addAttribute("error", "user_exists");
            return "redirect:/register";
        } catch (WeakPasswordException e) {
            redirectAttributes.addAttribute("error", "weak_password");
            redirectAttributes.addFlashAttribute("failMessages", e.getFailMessages());
            return "redirect:/register";
        }
    }

    @GetMapping("/register-totp")
    String totpRegistrationPage(Model model,
                                HttpServletRequest request) {
        AppUser appUser = (AppUser) request.getSession().getAttribute("user");
        if (appUser == null) {
            throw new RegistrationException();
        }

        String totpQrCode = totpService.generateTotpQrCode(appUser).orElseThrow();
        TotpCodeDto totpCodeDto = new TotpCodeDto();

        model.addAttribute("totpCodeDto", totpCodeDto);
        model.addAttribute("totpQr", totpQrCode);
        return "register-totp";
    }

    @GetMapping("/register-confirm")
    String totpConfirmPage(Model model) {
        model.addAttribute("totpCodeDto", new TotpCodeDto());
        return "register-confirm";
    }

    @PostMapping("/register-confirm")
    String totpRegistrationConfirm(TotpCodeDto totpCodeDto,
                                   RedirectAttributes redirectAttributes,
                                   HttpServletRequest request) {
        AppUser appUser = (AppUser) request.getSession().getAttribute("user");

        try {
            registrationService.confirmUserRegistration(appUser.getId(), totpCodeDto);
        } catch (RegistrationException e) {
            request.getSession().invalidate();
            redirectAttributes.addAttribute("error", "registration_failed");
            return "redirect:/register";
        } catch (TotpCodeException e) {
            redirectAttributes.addAttribute("error", "invalid_totp");
            return "redirect:/register-confirm";
        }

        redirectAttributes.addAttribute("status", "registration_success");
        return "redirect:/login";
    }

}
