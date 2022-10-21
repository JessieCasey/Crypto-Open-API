package com.doubleA.auth;

import com.doubleA.auth.dto.LoginDTO;
import com.doubleA.auth.dto.SignupDTO;
import com.doubleA.auth.dto.TokenDTO;
import com.doubleA.security.jwt.TokenGenerator;
import com.doubleA.user.User;
import com.doubleA.user.UserManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    final UserManager userManager;
    final TokenGenerator tokenGenerator;
    final DaoAuthenticationProvider daoAuthenticationProvider;
    final JwtAuthenticationProvider refreshTokenAuthProvider;

    public AuthController(UserManager userManager, TokenGenerator tokenGenerator, DaoAuthenticationProvider daoAuthenticationProvider, @Qualifier("jwtRefreshTokenAuthProvider") JwtAuthenticationProvider refreshTokenAuthProvider) {
        this.userManager = userManager;
        this.tokenGenerator = tokenGenerator;
        this.daoAuthenticationProvider = daoAuthenticationProvider;
        this.refreshTokenAuthProvider = refreshTokenAuthProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignupDTO signupDTO, HttpServletRequest request) {
        User user = new User(signupDTO.getEmail(), signupDTO.getUsername(), signupDTO.getPassword());
        userManager.createUser(user, getSiteURL(request));

        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                user, signupDTO.getPassword(), Collections.EMPTY_LIST);

        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        Authentication authentication = daoAuthenticationProvider.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(loginDTO.getUsername(), loginDTO.getPassword()));

        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }

    @PostMapping("/token")
    public ResponseEntity<?> token(@RequestBody TokenDTO tokenDTO) {
        Authentication authentication = refreshTokenAuthProvider.authenticate(
                new BearerTokenAuthenticationToken(tokenDTO.getRefreshToken()));
        Jwt jwt = (Jwt) authentication.getCredentials();
        // check if present in db and not revoked, etc

        return ResponseEntity.ok(tokenGenerator.createToken(authentication));
    }

    @GetMapping("/verify/{code}")
    public String verifyUser(@RequestParam String code) {
        if (userManager.verify(code)) {
            return "verify_success";
        } else {
            return "verify_fail";
        }
    }
}