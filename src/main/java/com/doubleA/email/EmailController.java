package com.doubleA.email;


import com.doubleA.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/verify")
@Slf4j
public class EmailController {
    final UserService userService;

    public EmailController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String verifyUser(@RequestParam String code) {
        log.info("[GET][EmailController]: method 'verifyUser'");
        String verify = userService.verify(code);
        if (!verify.isEmpty()) {
            return verify;
        } else {
            return "verify_fail";
        }
    }
}