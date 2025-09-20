package com.coderank.executor.auth;


import com.coderank.executor.auth.dto.AuthResponse;
import com.coderank.executor.auth.dto.LoginRequest;
import com.coderank.executor.auth.dto.RegisterRequest;
import com.coderank.executor.security.JwtService;
import com.coderank.executor.user.User;
import com.coderank.executor.user.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtService jwt;
    private final long expiryMinutes;


    public AuthService(UserRepository users, PasswordEncoder encoder, AuthenticationManager authManager, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwt = jwt;
        this.expiryMinutes = 30; // mirror application.yml
    }


    public AuthResponse register(RegisterRequest req) {
        if (users.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User u = new User();
        u.setEmail(req.getEmail());
        u.setPasswordHash(encoder.encode(req.getPassword()));
        u.setRole("USER");
        users.save(u);
        String token = jwt.generateToken(u);
        return new AuthResponse(token, expiryMinutes * 60);
    }


    public AuthResponse login(LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        User principal = (User) auth.getPrincipal();
        String token = jwt.generateToken(principal);
        return new AuthResponse(token, expiryMinutes * 60);
    }
}
