package com.nexusgear.controller;

import com.nexusgear.dto.AuthResponse;
import com.nexusgear.dto.RegisterRequest;
import com.nexusgear.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
