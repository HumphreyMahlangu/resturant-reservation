package com.maisonverre.reservation;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration
class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter(JwtTokenService tokens) {
        return new JwtAuthenticationFilter(tokens);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/reservations").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/reservations").permitAll()
                        .requestMatchers("/api/auth/**", "/error").permitAll()
                        .anyRequest().hasRole("ADMIN"))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    CommandLineRunner seedAdminUser(
            RoleRepository roles,
            UserAccountRepository users,
            PasswordEncoder encoder,
            @Value("${app.admin.email:admin@maisonverre.local}") String email,
            @Value("${app.admin.password:change-me-now}") String password) {
        return args -> {
            Role adminRole = roles.findByName("ADMIN")
                    .orElseGet(() -> roles.save(new Role("ADMIN", "Restaurant administrator")));
            if (users.findByEmail(email.toLowerCase()).isEmpty()) {
                UserAccount admin = new UserAccount("Maison Verre Admin", email.toLowerCase(), null, adminRole);
                admin.update(admin.getName(), admin.getEmail(), null, encoder.encode(password), adminRole, "ACTIVE");
                users.save(admin);
            }
        };
    }
}

@Configuration
class JwtConfiguration {
    @Bean
    JwtTokenService jwtTokenService(
            @Value("${app.security.jwt-secret:VGhpcy1pcy1hLWRldi1zZWNyZXQta2V5LWZvci1tYWlzb24tdmVycmUtaXMtMzItYnl0ZXM=}") String secret,
            @Value("${app.security.jwt-expiration:PT8H}") Duration expiration) {
        return new JwtTokenService(secret, expiration);
    }
}

final class JwtTokenService {
    private final SecretKey key;
    private final Duration expiration;

    JwtTokenService(String base64Secret, Duration expiration) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.expiration = expiration;
    }

    String create(String email, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(key)
                .compact();
    }

    Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}

class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService tokens;

    JwtAuthenticationFilter(JwtTokenService tokens) {
        this.tokens = tokens;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                Claims claims = tokens.parse(header.substring(7));
                String role = claims.get("role", String.class);
                if (role != null) {
                    var authority = new SimpleGrantedAuthority("ROLE_" + role);
                    var authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            claims.getSubject(), null, java.util.List.of(authority));
                    org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (RuntimeException ignored) {
                // Invalid or expired tokens remain unauthenticated and are rejected by Spring Security.
            }
        }
        chain.doFilter(request, response);
    }
}
