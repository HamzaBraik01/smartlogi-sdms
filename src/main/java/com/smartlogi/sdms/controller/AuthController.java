package com.smartlogi.sdms.controller;

import com.smartlogi.sdms.config.security.CustomUserDetails;
import com.smartlogi.sdms.config.security.JwtTokenProvider;
import com.smartlogi.sdms.dto.auth.LoginRequest;
import com.smartlogi.sdms.dto.auth.LoginResponse;
import com.smartlogi.sdms.dto.auth.RegisterRequest;
import com.smartlogi.sdms.entity.ClientExpediteur;
import com.smartlogi.sdms.entity.GestionnaireLogistique;
import com.smartlogi.sdms.entity.Livreur;
import com.smartlogi.sdms.entity.Utilisateur;
import com.smartlogi.sdms.entity.enumeration.RoleUtilisateur;
import com.smartlogi.sdms.exception.ResourceAlreadyExistsException;
import com.smartlogi.sdms.repository.UtilisateurRepository;
import com.smartlogi.sdms.repository.ZoneRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/auth")
@Tag(name = "Authentification", description = "API pour l'authentification et la gestion des sessions")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final ZoneRepository zoneRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider jwtTokenProvider,
                          UtilisateurRepository utilisateurRepository,
                          PasswordEncoder passwordEncoder,
                          ZoneRepository zoneRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.zoneRepository = zoneRepository;
    }


    @Operation(summary = "Connexion utilisateur",
            description = "Authentifie un utilisateur avec email et mot de passe, retourne un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie"),
            @ApiResponse(responseCode = "401", description = "Email ou mot de passe incorrect")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtTokenProvider.generateToken(authentication);

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            LoginResponse response = LoginResponse.builder()
                    .token(jwt)
                    .expiresIn(jwtTokenProvider.getExpirationMs())
                    .userId(userDetails.getId())
                    .email(userDetails.getUsername())
                    .nom(userDetails.getNom())
                    .prenom(userDetails.getPrenom())
                    .role(userDetails.getRole())
                    .build();

            logger.info("Connexion réussie pour l'utilisateur: {}", loginRequest.getEmail());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            logger.warn("Tentative de connexion échouée pour: {}", loginRequest.getEmail());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Email ou mot de passe incorrect"));
        }
    }


    @Operation(summary = "Inscription d'un nouvel utilisateur",
            description = "Crée un nouveau compte utilisateur (accessible aux managers)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou email déjà utilisé")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Un utilisateur avec cet email existe déjà");
        }

        Utilisateur utilisateur = createUtilisateurFromRequest(registerRequest);
        utilisateur.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Utilisateur savedUser = utilisateurRepository.save(utilisateur);

        logger.info("Nouvel utilisateur créé: {} avec le rôle {}",
                savedUser.getEmail(), savedUser.getRole());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Utilisateur créé avec succès",
                        "userId", savedUser.getId(),
                        "email", savedUser.getEmail()
                ));
    }


    @Operation(summary = "Informations de l'utilisateur connecté",
            description = "Retourne les informations du profil de l'utilisateur authentifié")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informations utilisateur"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return ResponseEntity.ok(Map.of(
                "userId", userDetails.getId(),
                "email", userDetails.getUsername(),
                "nom", userDetails.getNom(),
                "prenom", userDetails.getPrenom(),
                "role", userDetails.getRole()
        ));
    }


    @Operation(summary = "Valider un token JWT",
            description = "Vérifie si un token JWT est valide et non expiré")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token valide"),
            @ApiResponse(responseCode = "401", description = "Token invalide ou expiré")
    })
    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.ok(Map.of(
                        "valid", true,
                        "email", jwtTokenProvider.getUsername(token)
                ));
            }
        }
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("valid", false, "message", "Token invalide ou expiré"));
    }


    private Utilisateur createUtilisateurFromRequest(RegisterRequest request) {
        Utilisateur utilisateur;

        switch (request.getRole()) {
            case ADMIN -> utilisateur = new com.smartlogi.sdms.entity.Admin();
            case GESTIONNAIRE -> utilisateur = new GestionnaireLogistique();
            case LIVREUR -> {
                Livreur livreur = new Livreur();
                livreur.setVehicule(request.getVehicule());
                if (request.getZoneId() != null) {
                    zoneRepository.findById(request.getZoneId())
                            .ifPresent(livreur::setZone);
                }
                utilisateur = livreur;
            }
            case CLIENT_EXPEDITEUR -> {
                ClientExpediteur client = new ClientExpediteur();
                client.setAdresse(request.getAdresse());
                utilisateur = client;
            }
            case DESTINATAIRE -> {
                com.smartlogi.sdms.entity.Destinataire destinataire = new com.smartlogi.sdms.entity.Destinataire();
                destinataire.setAdresse(request.getAdresse());
                utilisateur = destinataire;
            }
            default -> throw new IllegalArgumentException("Rôle non supporté: " + request.getRole());
        }

        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setTelephone(request.getTelephone());

        return utilisateur;
    }
}

