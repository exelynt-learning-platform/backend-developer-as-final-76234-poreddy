package com.example.booking.controller;

import com.example.booking.entity.AppUser;
import com.example.booking.repository.UserRepository;
import com.example.booking.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {
    record LoginRequest(String username, String password) {}
    record LoginResponse(String token) {}

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.username(), req.password()));
        AppUser u = userRepository.findByUsername(req.username()).orElseThrow();
        Set<String> roles = u.getRoles().stream().map(Enum::name).collect(Collectors.toSet());
        String token = jwtUtil.generateToken(u.getUsername(), roles);
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
