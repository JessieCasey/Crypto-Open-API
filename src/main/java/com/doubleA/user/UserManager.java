package com.doubleA.user;

import com.doubleA.apikey.ApiKey;
import com.doubleA.apikey.ApiKeyRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;

@Service
public class UserManager implements UserDetailsManager {

    final UserRepository userRepository;

    final ApiKeyRepository apiKeyRepository;

    final PasswordEncoder passwordEncoder;

    public UserManager(UserRepository userRepository, ApiKeyRepository apiKeyRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.apiKeyRepository = apiKeyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void createUser(UserDetails user) {
        try {
            ((User) user).setPassword(passwordEncoder.encode(user.getPassword()));
            ((User) user).setApikey(apiKeyRepository.insert(new ApiKey()));

            userRepository.save((User) user);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
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
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(MessageFormat.format("username {0} not found", username)));
    }
}