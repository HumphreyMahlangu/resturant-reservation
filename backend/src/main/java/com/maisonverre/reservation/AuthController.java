package com.maisonverre.reservation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ApiController.ORIGIN)
class AuthController {
    private final UserAccountRepository users;
    private final PasswordEncoder passwords;
    private final JwtTokenService tokens;

    AuthController(UserAccountRepository users, PasswordEncoder passwords, JwtTokenService tokens) {
        this.users = users;
        this.passwords = passwords;
        this.tokens = tokens;
    }

    @PostMapping("/login")
    AuthResponse login(@Valid @RequestBody LoginRequest request) {
        UserAccount user = users.findByEmailWithRole(request.email().trim().toLowerCase())
                .orElseThrow(() -> unauthorized());
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())
                || user.getRole() == null
                || !"ADMIN".equalsIgnoreCase(user.getRole().getName())
                || user.getPasswordHash() == null
                || !passwords.matches(request.password(), user.getPasswordHash())) {
            throw unauthorized();
        }
        return new AuthResponse(tokens.create(user.getEmail(), "ADMIN"), user.getName(), user.getEmail());
    }

    private static ResponseStatusException unauthorized() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid admin credentials");
    }

    record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
    record AuthResponse(String token, String name, String email) {}
}
