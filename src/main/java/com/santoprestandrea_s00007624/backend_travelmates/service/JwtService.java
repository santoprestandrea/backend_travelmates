package com.santoprestandrea_s00007624.backend_travelmates.service;

import com.santoprestandrea_s00007624.backend_travelmates.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    // Read the secret key from the .env file
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Read the token duration from the .env file (default 24 hours)
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    /**
     * GENERATES A JWT TOKEN
     *
     * When the user logs in, we create a token that contains:
     * - User's email (subject)
     * - User ID (custom claim)
     * - Name (custom claim)
     * - Role (custom claim)
     * - Creation date
     * - Expiration date
     *
     * The token is SIGNED with the secret key, so no one can modify it.
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(user.getEmail()) // Who is the user (email)
                .claim("userId", user.getId()) // Additional info: ID
                .claim("firstName", user.getFirstName()) // Name
                .claim("role", user.getRole().toString()) // Role (e.g., ADMIN)
                .setIssuedAt(now) // When it was created
                .setExpiration(expiryDate) // When it expires
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Sign with secret key
                .compact(); // Create the final string
    }

    /**
     * VALIDATES A TOKEN
     *
     * Checks if:
     * 1. The signature is correct (= no one has modified it)
     * 2. The token is not expired
     * 3. The format is valid
     *
     * Returns true if everything is ok, false otherwise.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // Use the same key to verify
                    .build()
                    .parseClaimsJws(token); // Check signature and expiration
            return true;
        } catch (MalformedJwtException ex) {
            logger.error("Malformed JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
        } catch (Exception ex) {
            logger.error("JWT validation error: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * EXTRACTS EMAIL FROM TOKEN
     *
     * Reads the "subject" of the token (which contains the email).
     */
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject(); // Returns the email
    }

    /**
     * EXTRACTS USER ID FROM TOKEN
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", Long.class);
    }

    /**
     * CONVERTS THE SECRET STRING TO A KEY OBJECT
     *
     * The JWT library needs a Key object, not a string.
     */
    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
