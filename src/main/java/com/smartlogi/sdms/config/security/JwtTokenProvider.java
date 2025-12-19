package com.smartlogi.sdms.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;


@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final Key signingKey;
    private final long jwtExpirationMs;

    public JwtTokenProvider(
            @Value("${security.jwt.secret}") String jwtSecret,
            @Value("${security.jwt.expiration-ms}") long jwtExpirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
    }


    public String generateToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("userId", userDetails.getId())
                .claim("roles", roles)
                .claim("nom", userDetails.getNom())
                .claim("prenom", userDetails.getPrenom())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }


    public String getUsername(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }


    public String getUserId(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", String.class);
    }


    public String getRoles(String token) {
        Claims claims = parseToken(token);
        return claims.get("roles", String.class);
    }


    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (SignatureException ex) {
            logger.error("Signature JWT invalide: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Token JWT malformé: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Token JWT expiré: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Token JWT non supporté: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("Claims JWT vide: {}", ex.getMessage());
        }
        return false;
    }


    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    public long getExpirationMs() {
        return jwtExpirationMs;
    }
}

