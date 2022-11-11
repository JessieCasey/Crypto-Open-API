package com.doubleA.user.service;

import com.doubleA.apikey.ApiKey;
import com.doubleA.apikey.ApiKeyRepository;
import com.doubleA.auth.dto.SignInDTO;
import com.doubleA.auth.dto.SignupDTO;
import com.doubleA.auth.token.RefreshToken;
import com.doubleA.auth.token.service.RefreshTokenService;
import com.doubleA.security.jwt.JwtResponse;
import com.doubleA.security.jwt.JwtUtils;
import com.doubleA.user.User;
import com.doubleA.user.UserRepository;
import com.doubleA.user.role.ERole;
import com.doubleA.user.role.Role;
import com.doubleA.user.role.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    public UserService(UserRepository userRepository, ApiKeyRepository apiKeyRepository,
                       JavaMailSender mailSender, @Value("${spring.mail.username}") String fromAddress,
                       RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils, AuthenticationManager authenticationManager,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.apiKeyRepository = apiKeyRepository;
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
    }

    public void createUser(SignupDTO request, String url) {
        User user = createUser(request);

        String content = "Dear " + user.getUsername() + ",<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "&Crypto - the best open crypto API.";

        String verifyURL = url + "/verify?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);

        sendEmail(user, content, "Please verify your registration");
    }

    public User createUser(SignupDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User(request.getUsername(), request.getEmail(),
                encoder.encode(request.getPassword()));

        Set<String> strRoles = request.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin" -> {
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                    }
                    case "mod" -> {
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                    }
                    default -> {
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                    }
                }
            });
        }
        user.setRoles(roles);

        try {
            ApiKey apiKey = new ApiKey();
            apiKeyRepository.insert(apiKey);
            user.setApikey(apiKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        user.setVerificationCode(RandomString.make(64));
        user.setEnabled(false);

        return userRepository.save(user);
    }

    public void sendEmail(User user, String content, String subject) {
        try {
            String toAddress = user.getEmail();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom(fromAddress, "DoubleA");
            helper.setTo(toAddress);
            helper.setSubject(subject);

            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String verify(String verificationCode) {
        log.info("Method 'verify': method is invoked");
        User user = userRepository.findByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            log.warn("Method 'verify': User is already enabled or null");
            throw new IllegalArgumentException();
        } else {
            log.info("Method 'verify': User '" + user.getUsername() + "' is verified");
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepository.save(user);

            String content = "Hi again! " + user.getUsername() + ",<br>"
                    + "There's your private API KEY (However, we call it C-KEY): <br>"
                    + "Your C-KEY: [[C-KEY]] <br>"
                    + "Feel a great power with this, <br>"
                    + "&Crypto - the best open crypto API.";

            String ckey = user.getApikey().getId();
            content = content.replace("[[C-KEY]]", ckey);

            log.info("Method 'verify': Try to send C-KEY to the '" + user.getUsername() + "'");
            sendEmail(user, content, "Please take your C-KEY");
            log.info("Method 'verify': Successful");
            return ckey;
        }
    }

    public Page<User> getPage(Query query, Pageable pageable) {
        return userRepository.findAll(query, pageable);
    }


    public void updateUser(UserDetails userDetails) {
        User user = (User) userDetails;
        if (userDetails != null && userExists(user.getUsername())) {
            userRepository.save(user);
        }
    }


    public void deleteUser(String username) {
        if (userExists(username)) {
            userRepository.deleteByUsername(username);
        }
    }

    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public JwtResponse authenticate(@Valid SignInDTO loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);
    }
}