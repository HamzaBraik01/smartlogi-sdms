# 📊 SmartLogi SDMS - Résumé Exécutif

## 🎯 Présentation du Projet

**SmartLogi Delivery Management System (SDMS)** est une plateforme de gestion des livraisons développée en Spring Boot, actuellement en production avec une authentification classique (email/mot de passe + JWT).

---

## 📈 État Actuel vs Évolution

| Aspect | État Actuel ✅ | Évolution Demandée 🚀 |
|--------|---------------|----------------------|
| **Authentification** | Email/Password + JWT | Hybride: Email/Password + OAuth2 → JWT |
| **Providers** | Local uniquement | Local + Google + Apple + Facebook + Okta |
| **Modèle User** | password obligatoire | password nullable (OAuth2) |
| **Champs User** | id, email, password, nom, prenom, role | + provider, providerId, enabled, imageUrl |
| **Rôle par défaut** | Défini à l'inscription | CLIENT_EXPEDITEUR pour OAuth2 |
| **JWT** | Après login classique | Après login classique OU OAuth2 |

---

## 🏗️ Architecture Technique

```
┌─────────────────────────────────────────────────────────────────────────┐
│                              FRONTEND                                    │
│                     (Angular 4200 / React 3000)                         │
└─────────────────────────────────────┬───────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                          NGINX REVERSE PROXY                            │
│                              (Port 80/443)                              │
└─────────────────────────────────────┬───────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         SPRING BOOT API (8080)                          │
├─────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌──────────────┐   │
│  │   Auth      │  │   OAuth2    │  │    Colis    │  │  Livreurs    │   │
│  │ Controller  │  │  Handlers   │  │ Controller  │  │  Controller  │   │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬───────┘   │
│         │                │                │                 │          │
│  ┌──────┴────────────────┴────────────────┴─────────────────┴──────┐   │
│  │                    SPRING SECURITY                               │   │
│  │  ┌────────────────┐  ┌────────────────┐  ┌────────────────────┐ │   │
│  │  │ JWT Filter     │  │ OAuth2 Login   │  │ Authorization      │ │   │
│  │  └────────────────┘  └────────────────┘  └────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                      SERVICES                                     │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                      │                                  │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │                     JPA REPOSITORIES                              │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────┬───────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────┐
│                         POSTGRESQL (5432)                               │
│                        smartlogi_management                             │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 👥 Profils Utilisateurs

| Rôle | Authority | Permissions Principales |
|------|-----------|------------------------|
| **ADMIN** | ROLE_ADMIN | Gestion rôles et permissions |
| **GESTIONNAIRE** | ROLE_MANAGER | Gestion livreurs, zones, statistiques |
| **LIVREUR** | ROLE_DELIVERY | Mise à jour statuts, colis assignés |
| **CLIENT_EXPEDITEUR** | ROLE_CLIENT | Création et suivi de ses colis |
| **DESTINATAIRE** | ROLE_VIEWER | Consultation suivi colis |

---

## 🔐 Flux d'Authentification

### Authentification Classique (Existant)

```
Client → POST /auth/login {email, password}
       → Backend valide credentials
       → Backend génère JWT
       → Client reçoit {token, userId, role, ...}
       → Client utilise "Authorization: Bearer {token}" pour API
```

### Authentification OAuth2 (Nouveau)

```
Client → Click "Login avec Google"
       → Redirect vers Google OAuth2
       → Utilisateur s'authentifie chez Google
       → Google redirect vers /login/oauth2/code/google
       → Backend échange code → access token
       → Backend récupère infos utilisateur
       → Backend crée/MAJ utilisateur en DB
       → Backend génère JWT INTERNE
       → Redirect vers frontend avec JWT
       → Client utilise "Authorization: Bearer {token}" pour API
```

---

## 📁 Fichiers Clés

### Configuration Sécurité

| Fichier | Rôle |
|---------|------|
| `SecurityConfig.java` | Configuration Spring Security |
| `JwtTokenProvider.java` | Génération et validation JWT |
| `JwtAuthenticationFilter.java` | Filtre extraction JWT |
| `CustomUserDetailsService.java` | Chargement utilisateur |

### Entités Principales

| Entité | Description |
|--------|-------------|
| `Utilisateur` | Classe abstraite utilisateur |
| `GestionnaireLogistique` | Gestionnaire (extends Utilisateur) |
| `Livreur` | Livreur (extends Utilisateur) |
| `ClientExpediteur` | Client (extends Utilisateur) |
| `Colis` | Colis à livrer |
| `Zone` | Zone géographique |

---

## 🐳 Déploiement Docker

### Services Existants

```yaml
services:
  postgres-db:     # PostgreSQL 15
  sdms-api:        # Spring Boot API
  nginx:           # Reverse Proxy (optionnel)
  sonarqube:       # Qualité code
```

### Ports Exposés

| Service | Port |
|---------|------|
| API | 8080 |
| PostgreSQL | 5432 |
| Nginx HTTP | 80 |
| Nginx HTTPS | 443 |
| SonarQube | 9000 |

---

## 📋 Actions Requises pour OAuth2

### 1. Modifications Base de Données
- [ ] Ajouter colonnes: `provider`, `provider_id`, `enabled`, `image_url`, `email_verified`
- [ ] Créer index sur `provider` + `provider_id`

### 2. Modifications Code
- [ ] Créer enum `AuthProvider`
- [ ] Modifier entité `Utilisateur`
- [ ] Ajouter dépendance `spring-boot-starter-oauth2-client`
- [ ] Créer `CustomOAuth2UserService`
- [ ] Créer `OAuth2AuthenticationSuccessHandler`
- [ ] Modifier `SecurityConfig`

### 3. Configuration
- [ ] Configurer providers dans `application.yml`
- [ ] Ajouter variables d'environnement OAuth2

### 4. Tests
- [ ] Tests unitaires OAuth2
- [ ] Tests intégration
- [ ] Mise à jour collections Postman

---

## 📞 Points de Contact

- **Développeur principal**: Hamza Braik
- **Email projet**: dev@smartlogi.com
- **Documentation API**: http://localhost:8080/swagger-ui.html

---

## 📚 Documentation Disponible

| Document | Description |
|----------|-------------|
| `docs/CONTEXTE_PROJET.md` | Analyse détaillée du projet actuel |
| `docs/IMPLEMENTATION_OAUTH2.md` | Guide technique d'implémentation |
| `docs/postman/` | Collections Postman |
| `README.md` | Documentation générale |

---

*SmartLogi SDMS © 2024*

