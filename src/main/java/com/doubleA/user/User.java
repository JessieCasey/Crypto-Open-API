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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Document
@Data
@Getter
@Setter
@NoArgsConstructor
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
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

}

//import com.doubleA.apikey.ApiKey;
//import lombok.Data;
//import lombok.NonNull;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.index.Indexed;
//import org.springframework.data.mongodb.core.mapping.DBRef;
//import org.springframework.data.mongodb.core.mapping.Document;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.time.LocalDateTime;
//import java.time.temporal.ChronoUnit;
//import java.util.Collection;
//import java.util.Collections;

//@Document
//@Data
//public class User implements UserDetails {
//
//    @Id
//    private String id;
//
//    @Indexed(unique = true)
//    @NonNull
//    private String username;
//
//    @Indexed(unique = true)
//    @NonNull
//    private String email;
//
//    @NonNull
//    private String password;
//
//    @DBRef
//    private ApiKey apikey;
//
//    private LocalDateTime date;
//
//    private LocalDateTime authorizationTime;
//
//    private String verificationCode;
//
//    private boolean enabled;
//
//    public User(@NonNull String email, @NonNull String username, @NonNull String password) {
//        this();
//        this.username = username;
//        this.email = email;
//        this.password = password;
//    }
//
//    public User() {
//        this.date = LocalDateTime.now();
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return Collections.EMPTY_LIST;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        long between = ChronoUnit.MONTHS.between(LocalDateTime.now(), authorizationTime);
//        return !(between > 6);
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return enabled;
//    }
//}

