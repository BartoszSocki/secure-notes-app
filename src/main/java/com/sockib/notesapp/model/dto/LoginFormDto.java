package com.sockib.notesapp.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginFormDto {

    private String username;
    private String password;
    private String totp;

}
