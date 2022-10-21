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
public class UserManager implements UserDetailsManager {

    final UserRepository userRepository;

    final ApiKeyRepository apiKeyRepository;

    final PasswordEncoder passwordEncoder;

    final JavaMailSender mailSender;

    final String fromAddress;

    public UserManager(UserRepository userRepository, ApiKeyRepository apiKeyRepository,
                       PasswordEncoder passwordEncoder, JavaMailSender mailSender,
                       @Value("${spring.mail.username}") String fromAddress) {
        this.userRepository = userRepository;
        this.apiKeyRepository = apiKeyRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void createUser(UserDetails user, String url) {
        try {
            ((User) user).setPassword(passwordEncoder.encode(user.getPassword()));
            ((User) user).setApikey(apiKeyRepository.insert(new ApiKey()));

            String randomCode = RandomString.make(64);
            ((User) user).setVerificationCode(randomCode);
            ((User) user).setEnabled(false);
            userRepository.save((User) user);

            sendVerificationEmail((User) user, url);
        } catch (NoSuchAlgorithmException | MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendVerificationEmail(User user, String siteURL)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = user.getEmail();
        String senderName = "DoubleA";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Your company name.";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", user.getUsername());
        String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }

    public boolean verify(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode);

        if (user == null || user.isEnabled()) {
            return false;
        } else {
            user.setVerificationCode(null);
            user.setEnabled(true);
            userRepository.save(user);

            return true;
        }

    }

    @Override
    public void createUser(UserDetails user) {

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
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(MessageFormat.format("username {0} not found", username)));
    }
}