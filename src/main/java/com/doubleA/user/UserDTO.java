package com.doubleA.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private String apikey;
    private boolean isPremium;
    private long credits;
    private List<User.Request> requests;

    public static UserDTO from(User user) {
        return builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .apikey(user.isEnabled() ? user.getApikey().getId() : "Please verify your email")
                .credits(user.getAvailableCredits())
                .isPremium(user.isUserPremium())
                .requests(user.getRequests())
                .build();
    }
}