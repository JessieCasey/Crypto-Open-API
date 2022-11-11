package com.doubleA.auth.token;

import com.doubleA.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.Instant;

@Document
@Data
@NoArgsConstructor
public class RefreshToken {

    @Id
    private String id;

    @DBRef
    private User user;

    private String token;

    private Instant expiryDate;
}