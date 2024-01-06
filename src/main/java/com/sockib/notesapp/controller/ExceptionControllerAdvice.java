package com.sockib.notesapp.controller;

import com.sockib.notesapp.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(value = AccessDeniedException.class)
    String handleAccessDeniedException(HttpServletRequest request, Exception e) {
        return "access-denied";
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    String handleResourceNotFoundException(HttpServletRequest request, Exception e) {
        return "404";
    }

}
