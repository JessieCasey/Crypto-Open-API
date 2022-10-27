package com.doubleA;

import com.doubleA.crypto.filter.ResourceRepositoryImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication
@EnableMongoRepositories(repositoryBaseClass = ResourceRepositoryImpl.class)
public class CryptoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CryptoApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
