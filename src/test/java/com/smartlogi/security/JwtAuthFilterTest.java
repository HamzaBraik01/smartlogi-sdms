package com.smartlogi.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    private UserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        testUserDetails = User.builder()
                .username("test@smartlogi.com")
                .password("password")
                .authorities("ROLE_GESTIONNAIRE")
                .build();
    }

    @Test
    void doFilterInternal_should_authenticate_user_with_valid_token() throws ServletException, IOException {
        // Given
        String validToken = "valid.jwt.token";
        String authHeader = "Bearer " + validToken;

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtils.validateToken(validToken)).thenReturn(true);
        when(jwtUtils.extractUsername(validToken)).thenReturn("test@smartlogi.com");
        when(jwtUtils.validateToken(validToken, "test@smartlogi.com")).thenReturn(true);
        when(userDetailsService.loadUserByUsername("test@smartlogi.com")).thenReturn(testUserDetails);

        // When
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("test@smartlogi.com", authentication.getName());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_GESTIONNAIRE")));

        verify(filterChain, times(1)).doFilter(request, response);
        verify(userDetailsService, times(1)).loadUserByUsername("test@smartlogi.com");
    }

    @Test
    void doFilterInternal_should_not_authenticate_without_authorization_header() throws ServletException, IOException {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtils, never()).validateToken(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void doFilterInternal_should_not_authenticate_with_invalid_token() throws ServletException, IOException {
        // Given
        String invalidToken = "invalid.jwt.token";
        String authHeader = "Bearer " + invalidToken;

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtils.validateToken(invalidToken)).thenReturn(false);

        // When
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void doFilterInternal_should_not_authenticate_without_bearer_prefix() throws ServletException, IOException {
        // Given
        String authHeader = "Basic something"; // Pas Bearer

        when(request.getHeader("Authorization")).thenReturn(authHeader);

        // When
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtils, never()).validateToken(anyString());
    }

    @Test
    void doFilterInternal_should_not_authenticate_when_token_does_not_match_user() throws ServletException, IOException {
        // Given
        String token = "valid.jwt.token";
        String authHeader = "Bearer " + token;

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(jwtUtils.extractUsername(token)).thenReturn("test@smartlogi.com");
        when(jwtUtils.validateToken(token, "test@smartlogi.com")).thenReturn(false); // Token ne correspond pas
        when(userDetailsService.loadUserByUsername("test@smartlogi.com")).thenReturn(testUserDetails);

        // When
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_should_continue_filter_chain_even_on_exception() throws ServletException, IOException {
        // Given
        String token = "valid.jwt.token";
        String authHeader = "Bearer " + token;

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtils.validateToken(token)).thenThrow(new RuntimeException("JWT parsing error"));

        // When
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);

        // Le filtre doit continuer même en cas d'erreur
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_should_not_override_existing_authentication() throws ServletException, IOException {
        // Given
        String token = "valid.jwt.token";
        String authHeader = "Bearer " + token;

        // Crée une authentification existante
        UserDetails existingUser = User.builder()
                .username("existing@smartlogi.com")
                .password("password")
                .authorities("ROLE_ADMIN")
                .build();

        SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        existingUser, null, existingUser.getAuthorities()
                )
        );

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtUtils.validateToken(token)).thenReturn(true);
        when(jwtUtils.extractUsername(token)).thenReturn("test@smartlogi.com");

        // When
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // Then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals("existing@smartlogi.com", authentication.getName()); // Garde l'authentification existante

        verify(filterChain, times(1)).doFilter(request, response);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }
}

