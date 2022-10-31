package com.doubleA.auth;

import com.doubleA.auth.dto.LoginDTO;
import com.doubleA.auth.dto.SignupDTO;
import com.doubleA.auth.dto.TokenDTO;
import com.doubleA.security.jwt.TokenGenerator;
import com.doubleA.user.User;
import com.doubleA.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    final UserService userService;
    final TokenGenerator tokenGenerator;
    final DaoAuthenticationProvider daoAuthenticationProvider;
    final JwtAuthenticationProvider refreshTokenAuthProvider;

    public AuthController(UserService userService, TokenGenerator tokenGenerator, DaoAuthenticationProvider daoAuthenticationProvider, @Qualifier("jwtRefreshTokenAuthProvider") JwtAuthenticationProvider refreshTokenAuthProvider) {
        this.userService = userService;
        this.tokenGenerator = tokenGenerator;
        this.daoAuthenticationProvider = daoAuthenticationProvider;
        this.refreshTokenAuthProvider = refreshTokenAuthProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<?> signUpUser(@RequestBody SignupDTO signupDTO, HttpServletRequest request) {
        log.info("[Get][AuthController] Request to method 'signUpUser'");
        try {
            User user = new User(signupDTO.getEmail(), signupDTO.getUsername(), signupDTO.getPassword());
            userService.createUser(user, getSiteURL(request));

            Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                    user, signupDTO.getPassword(), Collections.EMPTY_LIST);

            return ResponseEntity.ok(tokenGenerator.createToken(authentication));
        } catch (Exception e) {
            log.error("Error in method 'signUpUser': " + e.getMessage());
            return ResponseEntity.badRequest().body(e);
        }

    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }

    @PostMapping("/login")
    public ResponseEntity<?> signInUser(@RequestBody LoginDTO loginDTO) {
        log.info("[Get][AuthController] Request to method 'signInUser'");
        try {
            Authentication authentication = daoAuthenticationProvider
                    .authenticate(UsernamePasswordAuthenticationToken.unauthenticated(loginDTO.getUsername(), loginDTO.getPassword()));

            return ResponseEntity.ok(tokenGenerator.createToken(authentication));
        } catch (Exception e) {
            log.error("Error in method 'signInUser': " + e.getMessage());
            return ResponseEntity.badRequest().body(e);
        }

    }

    @PostMapping("/token")
    public ResponseEntity<?> getUserToken(@RequestBody TokenDTO tokenDTO) {
        log.info("[Post][AuthController] Request to method 'getUserToken'");
        try {
            Authentication authentication = refreshTokenAuthProvider.authenticate(
                    new BearerTokenAuthenticationToken(tokenDTO.getRefreshToken()));
            Jwt jwt = (Jwt) authentication.getCredentials();
            // check if present in db and not revoked, etc

            return ResponseEntity.ok(tokenGenerator.createToken(authentication));
        } catch (Exception e) {
            log.error("Error in method 'getUserToken': " + e.getMessage());
            return ResponseEntity.badRequest().body(e);
        }

    }
}