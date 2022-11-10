package com.doubleA.user;

import com.doubleA.crypto.filter.repository.ResourceRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String>, ResourceRepository<User, String> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    User findByVerificationCode(String code);

    void deleteByUsername(String username);

    Boolean existsByEmail(String email);
}