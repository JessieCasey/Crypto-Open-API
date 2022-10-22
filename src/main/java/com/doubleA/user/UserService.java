package com.doubleA.user;

import com.doubleA.apikey.ApiKey;
import com.doubleA.apikey.ApiKeyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import net.bytebuddy.utility.RandomString;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;

@Service
@Slf4j
public class UserService implements UserDetailsManager {

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

    @Override
    public void createUser(UserDetails userDetails) {
        User user = (User) userDetails;
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setVerificationCode(RandomString.make(64));
        user.setEnabled(false);
        userRepository.save(user);
    }

    public void createUser(UserDetails user, String url) {
        try {
            createUser(user);
            sendVerificationEmail((User) user, url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: create SendEmail(User user, MimeMessage message);
    private void sendVerificationEmail(User user, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String content = "Dear " + user.getUsername() + ",<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "&Crypto - the best open crypto API.";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, "DoubleA");
        helper.setTo(toAddress);
        helper.setSubject("Please verify your registration");

        String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();
        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }

    public String sendApiKey(User user) {
        try {
            user.setApikey(apiKeyRepository.insert(new ApiKey()));
            userRepository.save(user);

            String toAddress = user.getEmail();
            String content = "Hi again! " + user.getUsername() + ",<br>"
                    + "There's your private API KEY (However, we call it C-KEY): <br>"
                    + "Your C-KEY: [[C-KEY]] <br>"
                    + "Feel a great power with this, <br>"
                    + "&Crypto - the best open crypto API.";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setFrom(fromAddress, "DoubleA");
            helper.setTo(toAddress);
            helper.setSubject("Please take your C-KEY");

            String ckey = user.getApikey().getId();
            content = content.replace("[[C-KEY]]", ckey);

            helper.setText(content, true);

            mailSender.send(message);
            return ckey;
        } catch (NoSuchAlgorithmException | MessagingException | UnsupportedEncodingException ignored) {
        }
        throw new IllegalArgumentException();
    }

    public String verify(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            throw new IllegalArgumentException();
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepository.save(user);

            return sendApiKey(user);
        }
    }


    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(MessageFormat.format("username {0} not found", username)));
    }
}