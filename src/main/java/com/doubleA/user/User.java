package com.doubleA.user;

import com.doubleA.apikey.ApiKey;
import com.doubleA.user.role.Role;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Document
@Data
@Getter
@Setter
public class User {
    @Id
    private String id;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    private Set<Role> roles = new HashSet<>();

    @DBRef
    private ApiKey apikey;

    private LocalDateTime date;

    private LocalDateTime authorizationTime;

    private String verificationCode;

    private boolean enabled;

    public User(@NotNull String username, @NotNull String email, @NotNull String password) {
        this();
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User() {
        this.date = LocalDateTime.now();
    }

    public boolean isAccountNonExpired() {
        long between = ChronoUnit.MONTHS.between(LocalDateTime.now(), authorizationTime);
        return !(between > 6);
    }
}

