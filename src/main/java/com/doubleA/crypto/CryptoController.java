package com.doubleA.crypto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crypto")
@Slf4j
public class CryptoController {

    @GetMapping("/get")
    public ResponseEntity<?> test() {
        log.info("[Get] Request to method 'test'");
        return ResponseEntity.ok("Hello world");
    }
}
