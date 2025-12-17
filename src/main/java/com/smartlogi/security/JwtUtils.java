package com.smartlogi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;


@Component
public class JwtUtils {
    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${security.jwt.secret:change-me-change-me-change-me-change-me-32-bytes}")
    private String jwtSecret;

    @Value("${security.jwt.expiration-ms:86400000}") // 24h par défaut
    private long jwtExpirationMs;

    public JwtUtils() {
    }

    // Constructeur utilitaire (tests/unitaires ou usage manuel)
    public JwtUtils(String jwtSecret, long jwtExpirationMs) {
        this.jwtSecret = Objects.requireNonNull(jwtSecret, "jwtSecret");
        this.jwtExpirationMs = jwtExpirationMs;
    }




    public String generateToken(String subject) {
        return generateToken(subject, Map.of());
    }


    public String generateToken(String subject, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiration = Date.from(now.plusMillis(jwtExpirationMs));

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }




    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception ex) {
            log.debug("Token JWT invalide: {}", ex.getMessage());
            return false;
        }
    }


    public boolean validateToken(String token, String expectedUsername) {
        try {
            String username = extractUsername(token);
            return Objects.equals(username, expectedUsername) && !isTokenExpired(token);
        } catch (Exception ex) {
            log.debug("Echec de validation du token: {}", ex.getMessage());
            return false;
        }
    }



    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }



    private Key getSigningKey() {
        byte[] keyBytes = decodeSecretToBytes(jwtSecret);
        // JJWT exige >= 256 bits pour HS256
        if (keyBytes.length < 32) {
            keyBytes = sha256(keyBytes); // étend à 256 bits de manière déterministe
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private static byte[] decodeSecretToBytes(String secret) {
        try {
            return Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException e) {
            return secret.getBytes(StandardCharsets.UTF_8);
        }
    }

    private static byte[] sha256(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return md.digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 non disponible", e);
        }
    }
}

