package com.doubleA.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInDTO {
    private String username;
    private String password;
}