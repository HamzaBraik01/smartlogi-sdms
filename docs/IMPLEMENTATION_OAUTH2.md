# 🔐 Guide d'Implémentation - Authentification Hybride OAuth2

## 📌 Introduction

Ce document détaille l'implémentation technique de l'authentification hybride pour SmartLogi SDMS, combinant l'authentification classique (email/password) et OAuth2 (Google, Apple, Facebook, Okta).

---

## 🏗️ Architecture Technique

### Diagramme de Séquence - OAuth2

```
┌────────┐          ┌───────────┐          ┌───────────┐          ┌──────────┐
│ Client │          │   SDMS    │          │  OAuth2   │          │   Base   │
│ (SPA)  │          │  Backend  │          │ Provider  │          │   Données│
└───┬────┘          └─────┬─────┘          └─────┬─────┘          └────┬─────┘
    │                     │                      │                      │
    │ 1. Click "Login     │                      │                      │
    │    avec Google"     │                      │                      │
    ├────────────────────►│                      │                      │
    │                     │                      │                      │
    │ 2. Redirect vers    │                      │                      │
    │    OAuth2 Provider  │                      │                      │
    │◄────────────────────┤                      │                      │
    │                     │                      │                      │
    │ 3. Authentification │                      │                      │
    │    chez Provider    │                      │                      │
    ├─────────────────────────────────────────►│                      │
    │                     │                      │                      │
    │ 4. Callback avec    │                      │                      │
    │    code OAuth2      │                      │                      │
    │◄─────────────────────────────────────────┤                      │
    │                     │                      │                      │
    │ 5. Redirect vers    │                      │                      │
    │    backend callback │                      │                      │
    ├────────────────────►│                      │                      │
    │                     │                      │                      │
    │                     │ 6. Échange code      │                      │
    │                     │    → Access Token    │                      │
    │                     ├─────────────────────►│                      │
    │                     │                      │                      │
    │                     │ 7. Récupérer         │                      │
    │                     │    user info         │                      │
    │                     ├─────────────────────►│                      │
    │                     │                      │                      │
    │                     │◄─────────────────────┤                      │
    │                     │ (email, name, id)    │                      │
    │                     │                      │                      │
    │                     │ 8. findOrCreate      │                      │
    │                     │    Utilisateur       │                      │
    │                     ├──────────────────────────────────────────►│
    │                     │                      │                      │
    │                     │◄──────────────────────────────────────────┤
    │                     │                      │                      │
    │                     │ 9. Générer JWT       │                      │
    │                     │    interne           │                      │
    │                     ├──────┐               │                      │
    │                     │      │               │                      │
    │                     │◄─────┘               │                      │
    │                     │                      │                      │
    │ 10. Redirect avec   │                      │                      │
    │     JWT token       │                      │                      │
    │◄────────────────────┤                      │                      │
    │                     │                      │                      │
```

---

## 📁 Structure des Fichiers à Créer/Modifier

```
src/main/java/com/smartlogi/sdms/
├── config/
│   └── security/
│       ├── SecurityConfig.java              ✏️ MODIFIER
│       └── oauth2/                          ✨ NOUVEAU PACKAGE
│           ├── OAuth2AuthenticationSuccessHandler.java
│           ├── OAuth2AuthenticationFailureHandler.java
│           ├── HttpCookieOAuth2AuthorizationRequestRepository.java
│           └── OAuth2Properties.java
│
├── dto/
│   └── auth/
│       ├── OAuth2UserInfo.java              ✨ NOUVEAU
│       ├── GoogleOAuth2UserInfo.java        ✨ NOUVEAU
│       ├── FacebookOAuth2UserInfo.java      ✨ NOUVEAU
│       ├── AppleOAuth2UserInfo.java         ✨ NOUVEAU
│       └── OAuth2LoginResponse.java         ✨ NOUVEAU
│
├── entity/
│   ├── Utilisateur.java                     ✏️ MODIFIER
│   └── enumeration/
│       └── AuthProvider.java                ✨ NOUVEAU
│
├── service/
│   ├── interfaces/
│   │   └── OAuth2UserService.java           ✨ NOUVEAU
│   └── impl/
│       ├── CustomOAuth2UserService.java     ✨ NOUVEAU
│       └── OAuth2UserServiceImpl.java       ✨ NOUVEAU
│
└── controller/
    └── OAuth2Controller.java                ✨ NOUVEAU

src/main/resources/
├── application.yml                          ✏️ MODIFIER
└── db/changelog/
    └── 009-add-oauth2-fields.xml           ✨ NOUVEAU
```

---

## 1️⃣ Étape 1 : Énumération AuthProvider

### Fichier : `AuthProvider.java`

```java
package com.smartlogi.sdms.entity.enumeration;

/**
 * Fournisseurs d'authentification supportés par SDMS.
 */
public enum AuthProvider {
    /**
     * Authentification locale (email/mot de passe)
     */
    LOCAL,
    
    /**
     * OAuth2 Google
     */
    GOOGLE,
    
    /**
     * OAuth2 Apple
     */
    APPLE,
    
    /**
     * OAuth2 Facebook
     */
    FACEBOOK,
    
    /**
     * OpenID Connect via Okta
     */
    OKTA
}
```

---

## 2️⃣ Étape 2 : Modification de l'Entité Utilisateur

### Fichier : `Utilisateur.java` (modifications)

```java
package com.smartlogi.sdms.entity;

import com.smartlogi.sdms.entity.enumeration.AuthProvider;
import com.smartlogi.sdms.entity.enumeration.RoleUtilisateur;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "utilisateur")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
public abstract class Utilisateur {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name = "nom")
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "telephone")
    private String telephone;

    // ⚠️ Devient nullable pour OAuth2
    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", insertable = false, updatable = false)
    private RoleUtilisateur role;
    
    // ✨ NOUVEAUX CHAMPS OAuth2
    
    /**
     * Fournisseur d'authentification (LOCAL par défaut)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private AuthProvider provider = AuthProvider.LOCAL;
    
    /**
     * ID unique de l'utilisateur chez le provider OAuth2
     */
    @Column(name = "provider_id")
    private String providerId;
    
    /**
     * URL de l'image de profil (récupérée depuis OAuth2)
     */
    @Column(name = "image_url")
    private String imageUrl;
    
    /**
     * Indique si le compte est activé
     */
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;
    
    /**
     * Indique si l'email a été vérifié
     */
    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;
}
```

---

## 3️⃣ Étape 3 : Migration Liquibase

### Fichier : `009-add-oauth2-fields.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-latest.xsd">

    <changeSet id="009-add-oauth2-fields" author="SmartLogi">
        <comment>Ajout des champs pour l'authentification OAuth2</comment>
        
        <!-- Ajouter la colonne provider -->
        <addColumn tableName="utilisateur">
            <column name="provider" type="VARCHAR(20)" defaultValue="LOCAL">
                <constraints nullable="false"/>
            </column>
        </addColumn>
        
        <!-- Ajouter la colonne provider_id -->
        <addColumn tableName="utilisateur">
            <column name="provider_id" type="VARCHAR(255)">
                <constraints nullable="true"/>
            </column>
        </addColumn>
        
        <!-- Ajouter la colonne image_url -->
        <addColumn tableName="utilisateur">
            <column name="image_url" type="VARCHAR(500)">
                <constraints nullable="true"/>
            </column>
        </addColumn>
        
        <!-- Ajouter la colonne enabled -->
        <addColumn tableName="utilisateur">
            <column name="enabled" type="BOOLEAN" defaultValueBoolean="true">
                <constraints nullable="false"/>
            </column>
        </addColumn>
        
        <!-- Ajouter la colonne email_verified -->
        <addColumn tableName="utilisateur">
            <column name="email_verified" type="BOOLEAN" defaultValueBoolean="false">
                <constraints nullable="false"/>
            </column>
        </addColumn>
        
        <!-- Index pour la recherche par provider + providerId -->
        <createIndex tableName="utilisateur" indexName="idx_utilisateur_provider_id">
            <column name="provider"/>
            <column name="provider_id"/>
        </createIndex>
        
    </changeSet>
    
    <!-- Mettre à jour les utilisateurs existants -->
    <changeSet id="009-update-existing-users" author="SmartLogi">
        <comment>Marquer les utilisateurs existants comme LOCAL et verified</comment>
        
        <update tableName="utilisateur">
            <column name="provider" value="LOCAL"/>
            <column name="enabled" valueBoolean="true"/>
            <column name="email_verified" valueBoolean="true"/>
            <where>provider IS NULL OR provider = ''</where>
        </update>
    </changeSet>

</databaseChangeLog>
```

### Mise à jour du changelog-master.xml

```xml
<include file="db/changelog/009-add-oauth2-fields.xml"/>
```

---

## 4️⃣ Étape 4 : DTOs OAuth2

### Fichier : `OAuth2UserInfo.java`

```java
package com.smartlogi.sdms.dto.auth;

import java.util.Map;

/**
 * Classe abstraite pour les informations utilisateur OAuth2.
 * Chaque provider a sa propre implémentation.
 */
public abstract class OAuth2UserInfo {
    
    protected Map<String, Object> attributes;
    
    protected OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
    
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    public abstract String getId();
    
    public abstract String getName();
    
    public abstract String getEmail();
    
    public abstract String getImageUrl();
    
    public String getFirstName() {
        String name = getName();
        if (name != null && name.contains(" ")) {
            return name.split(" ")[0];
        }
        return name;
    }
    
    public String getLastName() {
        String name = getName();
        if (name != null && name.contains(" ")) {
            String[] parts = name.split(" ");
            return parts[parts.length - 1];
        }
        return "";
    }
}
```

### Fichier : `GoogleOAuth2UserInfo.java`

```java
package com.smartlogi.sdms.dto.auth;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {
    
    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }
    
    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }
    
    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
    
    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
    
    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
    
    @Override
    public String getFirstName() {
        String givenName = (String) attributes.get("given_name");
        return givenName != null ? givenName : super.getFirstName();
    }
    
    @Override
    public String getLastName() {
        String familyName = (String) attributes.get("family_name");
        return familyName != null ? familyName : super.getLastName();
    }
}
```

### Fichier : `FacebookOAuth2UserInfo.java`

```java
package com.smartlogi.sdms.dto.auth;

import java.util.Map;

public class FacebookOAuth2UserInfo extends OAuth2UserInfo {
    
    public FacebookOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }
    
    @Override
    public String getId() {
        return (String) attributes.get("id");
    }
    
    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
    
    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
    
    @Override
    public String getImageUrl() {
        if (attributes.containsKey("picture")) {
            Map<String, Object> pictureObj = (Map<String, Object>) attributes.get("picture");
            if (pictureObj.containsKey("data")) {
                Map<String, Object> dataObj = (Map<String, Object>) pictureObj.get("data");
                if (dataObj.containsKey("url")) {
                    return (String) dataObj.get("url");
                }
            }
        }
        return null;
    }
    
    @Override
    public String getFirstName() {
        String firstName = (String) attributes.get("first_name");
        return firstName != null ? firstName : super.getFirstName();
    }
    
    @Override
    public String getLastName() {
        String lastName = (String) attributes.get("last_name");
        return lastName != null ? lastName : super.getLastName();
    }
}
```

### Fichier : `AppleOAuth2UserInfo.java`

```java
package com.smartlogi.sdms.dto.auth;

import java.util.Map;

public class AppleOAuth2UserInfo extends OAuth2UserInfo {
    
    public AppleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }
    
    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }
    
    @Override
    public String getName() {
        String firstName = getFirstName();
        String lastName = getLastName();
        
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return (String) attributes.get("email");
    }
    
    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
    
    @Override
    public String getImageUrl() {
        // Apple ne fournit pas d'image de profil
        return null;
    }
    
    @Override
    public String getFirstName() {
        if (attributes.containsKey("name")) {
            Map<String, Object> nameObj = (Map<String, Object>) attributes.get("name");
            return (String) nameObj.get("firstName");
        }
        return null;
    }
    
    @Override
    public String getLastName() {
        if (attributes.containsKey("name")) {
            Map<String, Object> nameObj = (Map<String, Object>) attributes.get("name");
            return (String) nameObj.get("lastName");
        }
        return null;
    }
}
```

### Fichier : `OAuth2UserInfoFactory.java`

```java
package com.smartlogi.sdms.dto.auth;

import com.smartlogi.sdms.entity.enumeration.AuthProvider;
import com.smartlogi.sdms.exception.OAuth2AuthenticationException;

import java.util.Map;

public class OAuth2UserInfoFactory {
    
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, 
                                                    Map<String, Object> attributes) {
        AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());
        
        return switch (provider) {
            case GOOGLE -> new GoogleOAuth2UserInfo(attributes);
            case FACEBOOK -> new FacebookOAuth2UserInfo(attributes);
            case APPLE -> new AppleOAuth2UserInfo(attributes);
            case OKTA -> new GoogleOAuth2UserInfo(attributes); // Okta utilise format similaire
            default -> throw new OAuth2AuthenticationException(
                    "Provider " + registrationId + " non supporté");
        };
    }
}
```

---

## 5️⃣ Étape 5 : Service OAuth2

### Fichier : `CustomOAuth2UserService.java`

```java
package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.dto.auth.OAuth2UserInfo;
import com.smartlogi.sdms.dto.auth.OAuth2UserInfoFactory;
import com.smartlogi.sdms.entity.ClientExpediteur;
import com.smartlogi.sdms.entity.Utilisateur;
import com.smartlogi.sdms.entity.enumeration.AuthProvider;
import com.smartlogi.sdms.exception.OAuth2AuthenticationException;
import com.smartlogi.sdms.repository.UtilisateurRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    private final UtilisateurRepository utilisateurRepository;
    
    public CustomOAuth2UserService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }
    
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) 
            throws OAuth2AuthenticationException {
        
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        
        return processOAuth2User(oAuth2UserRequest, oAuth2User);
    }
    
    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, 
                                         OAuth2User oAuth2User) {
        
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId, oAuth2User.getAttributes());
        
        if (oAuth2UserInfo.getEmail() == null || oAuth2UserInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException(
                    "Email non trouvé dans les informations OAuth2");
        }
        
        Optional<Utilisateur> utilisateurOptional = utilisateurRepository
                .findByEmail(oAuth2UserInfo.getEmail());
        
        Utilisateur utilisateur;
        
        if (utilisateurOptional.isPresent()) {
            utilisateur = utilisateurOptional.get();
            
            // Vérifier que le provider correspond
            AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());
            if (!utilisateur.getProvider().equals(provider) 
                    && !utilisateur.getProvider().equals(AuthProvider.LOCAL)) {
                throw new OAuth2AuthenticationException(
                        "Cet email est déjà associé à un compte " + utilisateur.getProvider());
            }
            
            // Mettre à jour les informations
            utilisateur = updateExistingUser(utilisateur, oAuth2UserInfo, registrationId);
        } else {
            // Créer un nouveau compte
            utilisateur = registerNewUser(oAuth2UserInfo, registrationId);
        }
        
        return new OAuth2UserPrincipal(utilisateur, oAuth2User.getAttributes());
    }
    
    private Utilisateur registerNewUser(OAuth2UserInfo oAuth2UserInfo, String registrationId) {
        // Par défaut, créer un ClientExpediteur (rôle par défaut pour OAuth2)
        ClientExpediteur utilisateur = new ClientExpediteur();
        
        utilisateur.setProvider(AuthProvider.valueOf(registrationId.toUpperCase()));
        utilisateur.setProviderId(oAuth2UserInfo.getId());
        utilisateur.setNom(oAuth2UserInfo.getLastName());
        utilisateur.setPrenom(oAuth2UserInfo.getFirstName());
        utilisateur.setEmail(oAuth2UserInfo.getEmail());
        utilisateur.setImageUrl(oAuth2UserInfo.getImageUrl());
        utilisateur.setEnabled(true);
        utilisateur.setEmailVerified(true);
        // Pas de mot de passe pour OAuth2
        utilisateur.setPassword(null);
        
        return utilisateurRepository.save(utilisateur);
    }
    
    private Utilisateur updateExistingUser(Utilisateur utilisateur, 
                                           OAuth2UserInfo oAuth2UserInfo,
                                           String registrationId) {
        // Mettre à jour le provider si c'était LOCAL
        if (utilisateur.getProvider().equals(AuthProvider.LOCAL)) {
            utilisateur.setProvider(AuthProvider.valueOf(registrationId.toUpperCase()));
            utilisateur.setProviderId(oAuth2UserInfo.getId());
        }
        
        // Mettre à jour les infos
        if (utilisateur.getNom() == null) {
            utilisateur.setNom(oAuth2UserInfo.getLastName());
        }
        if (utilisateur.getPrenom() == null) {
            utilisateur.setPrenom(oAuth2UserInfo.getFirstName());
        }
        if (utilisateur.getImageUrl() == null) {
            utilisateur.setImageUrl(oAuth2UserInfo.getImageUrl());
        }
        
        utilisateur.setEmailVerified(true);
        
        return utilisateurRepository.save(utilisateur);
    }
}
```

### Fichier : `OAuth2UserPrincipal.java`

```java
package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.entity.Utilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class OAuth2UserPrincipal implements OAuth2User {
    
    private final Utilisateur utilisateur;
    private final Map<String, Object> attributes;
    
    public OAuth2UserPrincipal(Utilisateur utilisateur, Map<String, Object> attributes) {
        this.utilisateur = utilisateur;
        this.attributes = attributes;
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = mapRoleToAuthority();
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
    
    @Override
    public String getName() {
        return utilisateur.getEmail();
    }
    
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }
    
    public String getId() {
        return utilisateur.getId();
    }
    
    public String getEmail() {
        return utilisateur.getEmail();
    }
    
    private String mapRoleToAuthority() {
        return switch (utilisateur.getRole()) {
            case ADMIN -> "ROLE_ADMIN";
            case GESTIONNAIRE -> "ROLE_MANAGER";
            case LIVREUR -> "ROLE_DELIVERY";
            case CLIENT_EXPEDITEUR -> "ROLE_CLIENT";
            case DESTINATAIRE -> "ROLE_VIEWER";
        };
    }
}
```

---

## 6️⃣ Étape 6 : Handler OAuth2 Success

### Fichier : `OAuth2AuthenticationSuccessHandler.java`

```java
package com.smartlogi.sdms.config.security.oauth2;

import com.smartlogi.sdms.config.security.JwtTokenProvider;
import com.smartlogi.sdms.service.impl.OAuth2UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    private final JwtTokenProvider tokenProvider;
    
    @Value("${app.oauth2.redirect-uri:http://localhost:4200/oauth2/redirect}")
    private String redirectUri;
    
    public OAuth2AuthenticationSuccessHandler(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        
        String targetUrl = determineTargetUrl(authentication);
        
        if (response.isCommitted()) {
            logger.debug("Response already committed. Cannot redirect to " + targetUrl);
            return;
        }
        
        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
    
    protected String determineTargetUrl(Authentication authentication) {
        // Générer le JWT pour l'utilisateur OAuth2
        String token = tokenProvider.generateTokenFromOAuth2(authentication);
        
        OAuth2UserPrincipal principal = (OAuth2UserPrincipal) authentication.getPrincipal();
        
        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .queryParam("userId", principal.getId())
                .queryParam("email", principal.getEmail())
                .build()
                .toUriString();
    }
}
```

### Fichier : `OAuth2AuthenticationFailureHandler.java`

```java
package com.smartlogi.sdms.config.security.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    
    @Value("${app.oauth2.redirect-uri:http://localhost:4200/oauth2/redirect}")
    private String redirectUri;
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, 
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("error", URLEncoder.encode(
                        exception.getLocalizedMessage(), StandardCharsets.UTF_8))
                .build()
                .toUriString();
        
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
```

---

## 7️⃣ Étape 7 : Mise à jour SecurityConfig

### Modifications à apporter à `SecurityConfig.java`

```java
package com.smartlogi.sdms.config.security;

// ... imports existants ...
import com.smartlogi.sdms.config.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.smartlogi.sdms.config.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.smartlogi.sdms.service.impl.CustomOAuth2UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomUserDetailsService userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;
    
    // ✨ Nouveaux composants OAuth2
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          CustomAccessDeniedHandler accessDeniedHandler,
                          CustomUserDetailsService userDetailsService,
                          CorsConfigurationSource corsConfigurationSource,
                          CustomOAuth2UserService customOAuth2UserService,
                          OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
                          OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.userDetailsService = userDetailsService;
        this.corsConfigurationSource = corsConfigurationSource;
        this.customOAuth2UserService = customOAuth2UserService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publics
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/oauth2/**").permitAll()           // ✨ OAuth2
                        .requestMatchers("/login/oauth2/**").permitAll()     // ✨ OAuth2
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        
                        // Endpoints protégés par rôle
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/livreurs/**").hasRole("MANAGER")
                        .requestMatchers("/api/v1/zones/**").hasRole("MANAGER")
                        
                        // Tout le reste
                        .anyRequest().authenticated())
                
                // ✨ Configuration OAuth2
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler))
                
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                
                .build();
    }
    
    // ... reste du code existant ...
}
```

---

## 8️⃣ Étape 8 : Configuration application.yml

### Ajouts au fichier `application.yml`

```yaml
spring:
  # ... configuration existante ...
  
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID:your-google-client-id}
            client-secret: ${GOOGLE_CLIENT_SECRET:your-google-client-secret}
            scope:
              - email
              - profile
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
          
          facebook:
            client-id: ${FACEBOOK_CLIENT_ID:your-facebook-client-id}
            client-secret: ${FACEBOOK_CLIENT_SECRET:your-facebook-client-secret}
            scope:
              - email
              - public_profile
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
          
          apple:
            client-id: ${APPLE_CLIENT_ID:your-apple-client-id}
            client-secret: ${APPLE_CLIENT_SECRET:your-apple-client-secret}
            authorization-grant-type: authorization_code
            client-authentication-method: client_secret_post
            scope:
              - email
              - name
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
          
          okta:
            client-id: ${OKTA_CLIENT_ID:your-okta-client-id}
            client-secret: ${OKTA_CLIENT_SECRET:your-okta-client-secret}
            scope:
              - openid
              - email
              - profile
            redirect-uri: "{baseUrl}/login/oauth2/code/{registrationId}"
        
        provider:
          apple:
            authorization-uri: https://appleid.apple.com/auth/authorize
            token-uri: https://appleid.apple.com/auth/token
            jwk-set-uri: https://appleid.apple.com/auth/keys
            user-name-attribute: sub
          
          okta:
            issuer-uri: ${OKTA_ISSUER_URI:https://your-domain.okta.com/oauth2/default}

# Configuration de l'application
app:
  oauth2:
    # URI de redirection après authentification OAuth2
    redirect-uri: ${OAUTH2_REDIRECT_URI:http://localhost:4200/oauth2/redirect}
    
    # Providers autorisés
    authorized-redirect-uris:
      - http://localhost:4200/oauth2/redirect
      - http://localhost:3000/oauth2/redirect
```

---

## 9️⃣ Étape 9 : Dépendances Maven

### Ajouts au `pom.xml`

```xml
<!-- OAuth2 Client -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

---

## 🧪 Tests Unitaires

### Fichier : `OAuth2ServiceTest.java`

```java
package com.smartlogi.sdms.service;

import com.smartlogi.sdms.dto.auth.GoogleOAuth2UserInfo;
import com.smartlogi.sdms.entity.ClientExpediteur;
import com.smartlogi.sdms.entity.enumeration.AuthProvider;
import com.smartlogi.sdms.repository.UtilisateurRepository;
import com.smartlogi.sdms.service.impl.CustomOAuth2UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth2ServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Test
    void testGoogleOAuth2UserInfo() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "123456789");
        attributes.put("email", "test@gmail.com");
        attributes.put("name", "John Doe");
        attributes.put("given_name", "John");
        attributes.put("family_name", "Doe");
        attributes.put("picture", "https://example.com/photo.jpg");
        
        GoogleOAuth2UserInfo userInfo = new GoogleOAuth2UserInfo(attributes);
        
        assertThat(userInfo.getId()).isEqualTo("123456789");
        assertThat(userInfo.getEmail()).isEqualTo("test@gmail.com");
        assertThat(userInfo.getName()).isEqualTo("John Doe");
        assertThat(userInfo.getFirstName()).isEqualTo("John");
        assertThat(userInfo.getLastName()).isEqualTo("Doe");
        assertThat(userInfo.getImageUrl()).isEqualTo("https://example.com/photo.jpg");
    }
}
```

---

## 📋 Checklist de Déploiement

### Variables d'Environnement Production

```bash
# Google OAuth2
GOOGLE_CLIENT_ID=your-production-client-id
GOOGLE_CLIENT_SECRET=your-production-client-secret

# Facebook OAuth2
FACEBOOK_CLIENT_ID=your-production-client-id
FACEBOOK_CLIENT_SECRET=your-production-client-secret

# Apple OAuth2
APPLE_CLIENT_ID=your-production-client-id
APPLE_CLIENT_SECRET=your-production-client-secret

# Okta
OKTA_CLIENT_ID=your-production-client-id
OKTA_CLIENT_SECRET=your-production-client-secret
OKTA_ISSUER_URI=https://your-domain.okta.com/oauth2/default

# Redirect URI
OAUTH2_REDIRECT_URI=https://your-production-domain.com/oauth2/redirect
```

---

## 🔗 Endpoints OAuth2 Disponibles

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/oauth2/authorize/google` | Initier connexion Google |
| GET | `/oauth2/authorize/facebook` | Initier connexion Facebook |
| GET | `/oauth2/authorize/apple` | Initier connexion Apple |
| GET | `/oauth2/authorize/okta` | Initier connexion Okta |
| GET | `/login/oauth2/code/{provider}` | Callback OAuth2 (géré automatiquement) |

---

*Document créé le 29/12/2024*
*Version: 1.0.0*

