package com.doubleA.apikey;

import com.doubleA.user.User;
import com.doubleA.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Component
@Slf4j
public class Interceptor implements HandlerInterceptor {

    public static final String APIKEY = "C_KEY";
    private final UserRepository userRepository;

    public Interceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) {
        if (request.getHeader(APIKEY) != null) {
            log.info("PostHandler: request invoked with APIKEY");
            User user = userRepository.findByApikey_Id(request.getHeader(APIKEY)).orElseThrow(() -> new UsernameNotFoundException("User with APIKEY not found"));
            if (user.isAvailableCredits()) {
                user.getRequests().add(new User.Request(response.getStatus() + "",
                        request.getRequestURI(), String.valueOf(LocalDateTime.now())));
                if (!request.getRequestURI().matches(".*\\b(auth|verify)\\b.*"))
                    user.setAvailableCredits(user.getAvailableCredits() - 1);
            }
            userRepository.save(user);
        } else {
            log.info("PostHandler is invoked: without APIKEY");
        }
    }
}
