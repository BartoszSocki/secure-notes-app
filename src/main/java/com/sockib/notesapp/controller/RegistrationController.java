package com.sockib.notesapp.controller;

import com.sockib.notesapp.exception.*;
import com.sockib.notesapp.model.dto.TotpCodeFormDto;
import com.sockib.notesapp.model.dto.UserRegistrationFormDto;
import com.sockib.notesapp.model.entity.AppUser;
import com.sockib.notesapp.service.RegistrationService;
import com.sockib.notesapp.service.impl.TotpServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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
        model.addAttribute("userRegistrationDto", new UserRegistrationFormDto());
        return "register";
    }

    // TODO: add validation
    @PostMapping("/register")
    String registerNewUser(UserRegistrationFormDto userRegistrationFormDto,
                           RedirectAttributes redirectAttributes,
                           HttpServletRequest request) {
        try {
            AppUser appUser = registrationService.registerNewUser(userRegistrationFormDto);
            request.getSession().setAttribute("user", appUser);

            return "redirect:/register-totp";
        } catch (PasswordMismatchException e) {
            redirectAttributes.addFlashAttribute("failMessages", List.of("password mismatch"));
            return "redirect:/register";
        } catch (UserAlreadyExistsException e) {
            redirectAttributes.addFlashAttribute("failMessages", List.of("user already exists"));
            return "redirect:/register";
        } catch (WeakPasswordException e) {
            redirectAttributes.addFlashAttribute("failMessages", e.getFailMessages());
            return "redirect:/register";
        } catch (InvalidUsernameException e) {
            redirectAttributes.addFlashAttribute("failMessages", List.of("invalid username"));
            return "redirect:/register";
        } catch (InvalidEmailException e) {
            redirectAttributes.addFlashAttribute("failMessages", List.of("invalid email"));
            return "redirect:/register";
        }
    }

    @GetMapping("/register-totp")
    String totpRegistrationPage(Model model,
                                HttpServletRequest request) {
        AppUser appUser = (AppUser) request.getSession().getAttribute("user");
        if (appUser == null) {
            return "redirect:/register";
        }

        String totpQrCode = totpService.generateTotpQrCode(appUser).orElseThrow();

        model.addAttribute("totpCodeDto", new TotpCodeFormDto());
        model.addAttribute("totpQr", totpQrCode);
        return "register-totp";
    }

    @GetMapping("/register-confirm")
    String totpConfirmPage(Model model) {
        model.addAttribute("totpCodeDto", new TotpCodeFormDto());
        return "register-confirm";
    }

    @PostMapping("/register-confirm")
    String totpRegistrationConfirm(TotpCodeFormDto totpCodeDto,
                                   RedirectAttributes redirectAttributes,
                                   HttpServletRequest request) {
        AppUser appUser = (AppUser) request.getSession().getAttribute("user");
        if (appUser == null) {
            return "redirect:/register";
        }

        try {
            registrationService.confirmUserRegistration(appUser.getId(), totpCodeDto);
        } catch (RegistrationException e) {
            request.getSession().invalidate();
            redirectAttributes.addFlashAttribute("failMessages", List.of("registration fail"));
            return "redirect:/register";
        } catch (TotpCodeException e) {
            redirectAttributes.addFlashAttribute("failMessages", List.of("bad totp code"));
            return "redirect:/register-confirm";
        }

        redirectAttributes.addFlashAttribute("successMessages", List.of("successful registration"));
        return "redirect:/login";
    }

}
