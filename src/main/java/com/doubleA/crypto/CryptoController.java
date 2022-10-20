package com.doubleA.crypto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crypto")
@Slf4j
public class CryptoController {
    // @CrossOrigin(origins = "http://localhost:8089")

    @GetMapping("/get")
    public ResponseEntity<?> test() {
        log.info("[Get] Request to method 'test'");
        return ResponseEntity.ok("Hello world");
    }
}
