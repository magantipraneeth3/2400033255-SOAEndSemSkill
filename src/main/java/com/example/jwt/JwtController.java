package com.example.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

@RestController
@RequestMapping("/api/jwt")
public class JwtController {
    private final JwtService jwtService;

    public JwtController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/generate")
    public GenerateResponse generate(@RequestBody Map<String, Object> payload) {
        if (payload == null || payload.isEmpty()) {
            throw new IllegalArgumentException("JSON payload must not be empty");
        }
        if (payload.containsKey("iss") || payload.containsKey("iat") || payload.containsKey("exp")) {
            throw new IllegalArgumentException("Do not provide iss, iat, or exp; the server sets these claims");
        }
        return new GenerateResponse(jwtService.generate(payload), "Bearer", 1800);
    }

    @PostMapping("/verify")
    public VerifyResponse verify(@Valid @RequestBody VerifyRequest request) {
        try {
            Claims claims = jwtService.verify(request.token());
            return new VerifyResponse(true, "JWT signature and claims are valid", claims);
        } catch (JwtException | IllegalArgumentException ex) {
            return new VerifyResponse(false, "Invalid or expired JWT: " + ex.getMessage(), null);
        }
    }

    public record VerifyRequest(@NotBlank String token) {}
    public record GenerateResponse(String accessToken, String tokenType, long expiresIn) {}
    public record VerifyResponse(boolean valid, String message, Map<String, Object> claims) {}

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
