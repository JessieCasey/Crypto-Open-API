package com.doubleA.auth;

import com.doubleA.auth.dto.SignInDTO;
import com.doubleA.auth.dto.SignupDTO;
import com.doubleA.auth.token.*;
import com.doubleA.auth.token.dto.TokenRefreshRequest;
import com.doubleA.auth.token.dto.TokenRefreshResponse;
import com.doubleA.auth.token.exception.TokenRefreshException;
import com.doubleA.auth.token.service.RefreshTokenService;
import com.doubleA.security.jwt.JwtUtils;
import com.doubleA.security.jwt.MessageResponse;
import com.doubleA.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    public AuthController(RefreshTokenService refreshTokenService, UserService userService, JwtUtils jwtUtils) {
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody SignInDTO loginRequest) {
        return ResponseEntity.ok(userService.authenticate(loginRequest));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken).map(refreshTokenService::verifyExpiration).map(RefreshToken::getUser).map(user -> {
            String token = jwtUtils.generateTokenFromUsername(user.getUsername());
            return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
        }).orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupDTO signUpRequest, HttpServletRequest request) {
        userService.createUser(signUpRequest, getSiteURL(request));
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }
}