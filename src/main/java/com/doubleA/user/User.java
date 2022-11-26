package com.doubleA.user;

import com.doubleA.apikey.ApiKey;
import com.doubleA.user.role.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private List<Request> requests = new ArrayList<>();

    private long availableCredits;

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
        this.availableCredits = 10000;
        getRequests().add(new User.Request(777 + "",
                "Hello world!", String.valueOf(LocalDateTime.now())));
    }

    public boolean isAccountNonExpired() {
        long between = ChronoUnit.MONTHS.between(LocalDateTime.now(), authorizationTime);
        return !(between > 6);
    }

    public boolean isUserPremium() {
        return this.getRoles().stream().anyMatch(x -> x.getName().name().equals("ROLE_PREMIUM"));
    }

    public boolean isAvailableCredits() {
        if (this.getRoles().stream().anyMatch(x -> x.getName().name().equals("ROLE_PREMIUM"))) {
            return availableCredits <= 100000;
        } else {
            return availableCredits <= 10000;
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {
        private String status;
        private String url;
        private String time;
    }


}

