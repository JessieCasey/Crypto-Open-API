package com.doubleA.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SignupDTO {
    private String username;
    private String password;
    private String email;
    private Set<String> role;
}