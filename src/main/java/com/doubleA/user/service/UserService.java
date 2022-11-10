package com.doubleA.user.service;

import com.doubleA.apikey.ApiKey;
import com.doubleA.apikey.ApiKeyRepository;
import com.doubleA.crypto.Crypto;
import com.doubleA.user.User;
import com.doubleA.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;

@Service
@Slf4j
public class UserService {

    final UserRepository userRepository;
    final ApiKeyRepository apiKeyRepository;
    final PasswordEncoder passwordEncoder;
    final JavaMailSender mailSender;
    final String fromAddress;

    public UserService(UserRepository userRepository, ApiKeyRepository apiKeyRepository,
                       PasswordEncoder passwordEncoder, JavaMailSender mailSender,
                       @Value("${spring.mail.username}") String fromAddress) {
        this.userRepository = userRepository;
        this.apiKeyRepository = apiKeyRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }


    public void createUser(UserDetails userDetails) {
        User user = (User) userDetails;
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            ApiKey apiKey = new ApiKey();
            apiKeyRepository.insert(apiKey);
            user.setApikey(apiKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        user.setVerificationCode(RandomString.make(64));
        user.setEnabled(false);
        userRepository.save(user);
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

    public void createUser(UserDetails user, String url) {
        createUser(user);

        String content = "Dear " + user.getUsername() + ",<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "&Crypto - the best open crypto API.";

        String verifyURL = url + "/verify?code=" + ((User) user).getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);

        sendEmail(((User) user), content, "Please verify your registration");
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


    public void changePassword(String oldPassword, String newPassword) {

    }


    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

}