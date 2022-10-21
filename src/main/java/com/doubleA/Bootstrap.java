package com.doubleA;

import com.doubleA.apikey.ApiKey;
import com.doubleA.apikey.ApiKeyRepository;
import com.doubleA.user.User;
import com.doubleA.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap {

    @Bean
    CommandLineRunner runner(UserRepository userRepository, PasswordEncoder passwordEncoder, ApiKeyRepository apiKeyRepository) {
        userRepository.deleteAll();
        apiKeyRepository.deleteAll();

        return args -> {
            User tony = new User();
            tony.setPassword(passwordEncoder.encode("1"));
            tony.setUsername("Tony");
            ApiKey apiKey = new ApiKey();
            apiKey.setId("a6081a8ecb80e7324d10453acc64bbd7");
            apiKeyRepository.insert(apiKey);
            tony.setApikey(apiKey);
            userRepository.insert(tony);
            System.out.println(tony.getApikey().getId());
        };
    }

}
