package com.crudzaso.crudcloud_backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Secure 256-bit key automatically generated for HS256
    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Token validity time → 1 hour (in milliseconds)
    private static final long EXPIRATION_TIME = 1000 * 60 * 60;

    /**
     * Generate a JWT token containing user email and role.
     * @param email user's email (used as subject)
     * @param role user's role (stored as a claim)
     * @return signed JWT token
     */
    public String generateToken(String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // store the role inside the token
        return createToken(claims, email);
    }

    /**
     * Create the actual token with claims and expiration date.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // custom data
                .setSubject(subject) // usually the username or email
                .setIssuedAt(new Date(System.currentTimeMillis())) // creation time
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // expires in 1h
                .signWith(SECRET_KEY) // sign using secure HS256 key
                .compact();
    }

    /**
     * Extract email (subject) from the token.
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract role from the token claims.
     */
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    /**
     * Extract any specific claim from the token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Parse and validate the token to get all its claims.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Validate if token is still valid (not expired and has subject).
     */
    public boolean isTokenValid(String token) {
        return extractEmail(token) != null && !isTokenExpired(token);
    }

    /**
     * Check if token has expired.
     */
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }
}
