# 📋 Contexte du Projet SmartLogi SDMS

## 🎯 Vue d'ensemble

**SmartLogi Delivery Management System (SDMS)** est une application de gestion dédiée aux activités de livraison, utilisée par différents profils d'utilisateurs. Cette documentation présente l'état actuel du projet et la feuille de route pour l'évolution vers une authentification hybride OAuth2.

---

## 📊 Architecture Actuelle

### Stack Technologique

| Composant | Technologie | Version |
|-----------|-------------|---------|
| **Backend** | Spring Boot | 3.5.7 |
| **Java** | JDK | 17 |
| **Base de données** | PostgreSQL | 15 |
| **ORM** | Spring Data JPA + Hibernate | - |
| **Migrations DB** | Liquibase | - |
| **Sécurité** | Spring Security + JWT | - |
| **JWT Library** | JJWT | 0.11.5 |
| **Documentation API** | SpringDoc OpenAPI | 2.7.0 |
| **Mapping** | MapStruct | 1.5.5 |
| **Tests** | JUnit 5 + Mockito | - |
| **Couverture code** | JaCoCo | 0.8.10 |
| **Qualité code** | SonarQube | - |
| **Conteneurisation** | Docker + Docker Compose | - |

### Structure du Projet

```
src/main/java/com/smartlogi/
├── sdms/
│   ├── config/
│   │   ├── security/           # Configuration sécurité (JWT, Auth)
│   │   ├── CorsConfig.java     # Configuration CORS
│   │   ├── SwaggerConfig.java  # Configuration OpenAPI
│   │   └── AppConfig.java
│   ├── controller/             # Contrôleurs REST
│   ├── dto/                    # Data Transfer Objects
│   │   ├── auth/               # DTOs authentification
│   │   └── admin/              # DTOs administration
│   ├── entity/                 # Entités JPA
│   │   └── enumeration/        # Énumérations
│   ├── exception/              # Gestion des exceptions
│   ├── mapper/                 # MapStruct mappers
│   ├── repository/             # Repositories JPA
│   │   └── specification/      # Specifications JPA
│   └── service/
│       ├── impl/               # Implémentations services
│       ├── interfaces/         # Interfaces services
│       └── security/           # Services sécurité
└── security/                   # Classes sécurité alternatives
```

---

## 👥 Modèle Utilisateur Actuel

### Hiérarchie des Entités (Single Table Inheritance)

```
Utilisateur (abstract)
├── Admin
├── GestionnaireLogistique
├── Livreur
├── ClientExpediteur
└── Destinataire
```

### Entité Utilisateur Actuelle

```java
@Entity
@Table(name = "utilisateur")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
public abstract class Utilisateur {
    private String id;           // UUID
    private String nom;
    private String prenom;
    private String email;        // unique
    private String telephone;
    private String password;
    private RoleUtilisateur role;
}
```

### Rôles Utilisateurs (Enumération)

| Rôle | Description | Authority Spring Security |
|------|-------------|---------------------------|
| `ADMIN` | Administrateur système | `ROLE_ADMIN` |
| `GESTIONNAIRE` | Gestionnaire logistique | `ROLE_MANAGER` |
| `LIVREUR` | Livreur | `ROLE_DELIVERY` |
| `CLIENT_EXPEDITEUR` | Client expéditeur | `ROLE_CLIENT` |
| `DESTINATAIRE` | Destinataire | `ROLE_VIEWER` |

---

## 🔐 Sécurité Actuelle

### Configuration Spring Security

**Fichier**: `SecurityConfig.java`

- **Session**: `STATELESS` (pas de session côté serveur)
- **CSRF**: Désactivé (API REST stateless)
- **CORS**: Configuré pour Angular (4200), React (3000), et localhost (8080)

### Endpoints Publics

```
/auth/**              → Authentification
/swagger-ui/**        → Documentation Swagger
/v3/api-docs/**       → OpenAPI Specs
/actuator/health      → Health check
```

### Endpoints Protégés

| Pattern | Autorisation |
|---------|--------------|
| `/api/v1/admin/**` | `ROLE_ADMIN` |
| `/api/v1/livreurs/**` | `ROLE_MANAGER` |
| `/api/v1/zones/**` | `ROLE_MANAGER` |
| `/api/v1/gestionnaires/**` | `ROLE_MANAGER` |
| `/api/v1/statistiques/**` | `ROLE_MANAGER` |
| `/api/v1/colis/**` | Authentifié |
| `/api/v1/clients/**` | Authentifié |
| `anyRequest()` | Authentifié |

### Flux d'Authentification Actuel (JWT)

```
┌─────────┐      POST /auth/login       ┌─────────────┐
│ Client  │ ──────────────────────────► │ AuthController│
│         │   {email, password}         │             │
└─────────┘                             └──────┬──────┘
                                               │
                                               ▼
                                   ┌───────────────────────┐
                                   │ AuthenticationManager │
                                   │  (DaoAuthProvider)    │
                                   └───────────┬───────────┘
                                               │
                                               ▼
                                   ┌───────────────────────┐
                                   │CustomUserDetailsService│
                                   │ (loadUserByUsername)  │
                                   └───────────┬───────────┘
                                               │
                                               ▼
                                   ┌───────────────────────┐
                                   │  JwtTokenProvider     │
                                   │  (generateToken)      │
                                   └───────────┬───────────┘
                                               │
┌─────────┐       JWT Token        ◄───────────┘
│ Client  │ ◄─────────────────────
└─────────┘
```

### Structure du JWT

```json
{
  "sub": "user@email.com",
  "userId": "uuid",
  "roles": "ROLE_MANAGER",
  "nom": "Nom",
  "prenom": "Prénom",
  "iat": 1234567890,
  "exp": 1234654290
}
```

### Configuration JWT

```yaml
security:
  jwt:
    secret: change-me-change-me-change-me-change-me-32-bytes
    expiration-ms: 86400000  # 24h
```

---

## 🚀 Évolution Demandée : Authentification Hybride

### Objectif Principal

Enrichir les modes d'accès à SDMS en proposant une **authentification hybride** :

1. **Authentification classique** : email/mot de passe → JWT
2. **Authentification OAuth2** : Google/Apple/Facebook → JWT interne

### Nouveaux Providers OAuth2

| Provider | Identifiant |
|----------|-------------|
| Google | `GOOGLE` |
| Apple | `APPLE` |
| Facebook | `FACEBOOK` |
| Okta (optionnel) | `OKTA` |
| Local (existant) | `LOCAL` |

---

## 📝 Évolutions du Modèle Métier

### Entité Utilisateur - Modifications Requises

```java
@Entity
@Table(name = "utilisateur")
public abstract class Utilisateur {
    // Champs existants
    private String id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String password;        // ⚠️ Devient NULLABLE pour OAuth2
    private RoleUtilisateur role;
    
    // ✨ NOUVEAUX CHAMPS
    private AuthProvider provider;  // LOCAL, GOOGLE, APPLE, FACEBOOK, OKTA
    private String providerId;      // ID unique du provider OAuth2
    private Boolean enabled;        // Activation du compte
}
```

### Nouvelle Énumération AuthProvider

```java
public enum AuthProvider {
    LOCAL,      // Authentification classique email/password
    GOOGLE,     // OAuth2 Google
    APPLE,      // OAuth2 Apple
    FACEBOOK,   // OAuth2 Facebook
    OKTA        // Serveur d'identité Okta
}
```

### Migration Liquibase Requise

```xml
<changeSet id="009-add-oauth2-fields" author="...">
    <addColumn tableName="utilisateur">
        <column name="provider" type="VARCHAR(20)" defaultValue="LOCAL"/>
        <column name="provider_id" type="VARCHAR(255)"/>
        <column name="enabled" type="BOOLEAN" defaultValueBoolean="true"/>
    </addColumn>
    
    <!-- Rendre password nullable -->
    <dropNotNullConstraint tableName="utilisateur" columnName="password"/>
</changeSet>
```

---

## 🔧 Architecture Cible - Authentification OAuth2

### Flux OAuth2 → JWT Interne

```
┌──────────┐     1. Login OAuth2      ┌─────────────────┐
│  Client  │ ───────────────────────► │  OAuth2 Provider │
│ (Web/App)│                          │ (Google/Apple/FB)│
└──────────┘                          └────────┬────────┘
     ▲                                         │
     │                                         │ 2. Code/Token
     │                                         ▼
     │                               ┌─────────────────────┐
     │                               │   SDMS Backend      │
     │                               │ OAuth2SuccessHandler│
     │                               └────────┬────────────┘
     │                                         │
     │                                         │ 3. Créer/MAJ User
     │                                         ▼
     │                               ┌─────────────────────┐
     │                               │ UtilisateurService  │
     │                               │ findOrCreateOAuth2  │
     │                               └────────┬────────────┘
     │                                         │
     │      5. JWT Interne                     │ 4. Générer JWT
     └─────────────────────────────────────────┘
```

### Nouveaux Composants à Créer

| Composant | Description |
|-----------|-------------|
| `OAuth2LoginSuccessHandler` | Handler après authentification OAuth2 réussie |
| `OAuth2UserService` | Récupère/crée l'utilisateur depuis OAuth2 |
| `AuthProvider` (enum) | Types de providers d'authentification |
| `OAuth2UserInfo` | DTO pour les infos utilisateur OAuth2 |
| `OAuth2Controller` | Endpoints spécifiques OAuth2 |

---

## 📦 Dépendances Maven à Ajouter

```xml
<!-- OAuth2 Client -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>

<!-- OAuth2 Resource Server (optionnel, pour validation tokens externes) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

---

## ⚙️ Configuration OAuth2 (application.yml)

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope:
              - email
              - profile
          
          facebook:
            client-id: ${FACEBOOK_CLIENT_ID}
            client-secret: ${FACEBOOK_CLIENT_SECRET}
            scope:
              - email
              - public_profile
          
          apple:
            client-id: ${APPLE_CLIENT_ID}
            client-secret: ${APPLE_CLIENT_SECRET}
            authorization-grant-type: authorization_code
            scope:
              - email
              - name
          
          okta:
            client-id: ${OKTA_CLIENT_ID}
            client-secret: ${OKTA_CLIENT_SECRET}
            scope:
              - openid
              - profile
              - email
        
        provider:
          okta:
            issuer-uri: ${OKTA_ISSUER_URI}
```

---

## 🐳 Configuration Docker

### docker-compose.app.yml (existant)

Le projet dispose déjà d'une configuration Docker complète :

- **PostgreSQL 15** : Base de données
- **Spring Boot API** : Backend SDMS
- **Nginx** : Reverse proxy (optionnel)

### Variables d'Environnement à Ajouter

```yaml
# OAuth2 Providers
GOOGLE_CLIENT_ID: ${GOOGLE_CLIENT_ID}
GOOGLE_CLIENT_SECRET: ${GOOGLE_CLIENT_SECRET}
FACEBOOK_CLIENT_ID: ${FACEBOOK_CLIENT_ID}
FACEBOOK_CLIENT_SECRET: ${FACEBOOK_CLIENT_SECRET}
APPLE_CLIENT_ID: ${APPLE_CLIENT_ID}
APPLE_CLIENT_SECRET: ${APPLE_CLIENT_SECRET}
OKTA_CLIENT_ID: ${OKTA_CLIENT_ID}
OKTA_CLIENT_SECRET: ${OKTA_CLIENT_SECRET}
OKTA_ISSUER_URI: ${OKTA_ISSUER_URI}
```

---

## 📋 Endpoints API - Vue d'ensemble

### Authentification Classique (Existant)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| POST | `/auth/login` | Connexion email/password |
| POST | `/auth/register` | Inscription nouvel utilisateur |
| GET | `/auth/me` | Profil utilisateur connecté |

### Authentification OAuth2 (À Créer)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/oauth2/authorize/{provider}` | Initier connexion OAuth2 |
| GET | `/oauth2/callback/{provider}` | Callback après OAuth2 |
| POST | `/oauth2/token` | Échanger token OAuth2 → JWT |

---

## 🧪 Tests Existants

- **JUnit 5** : Tests unitaires
- **Mockito** : Mocks pour tests
- **H2** : Base de données en mémoire pour tests
- **JaCoCo** : Rapport de couverture

---

## 📁 Collections Postman

Le projet inclut des collections Postman complètes :

- `SmartLogi_Admin_API.postman_collection.json`
- `SmartLogi_Client_API.postman_collection.json`
- `SmartLogi_Livreur_API.postman_collection.json`
- `SmartLogi_Manager_API.postman_collection.json`
- `SmartLogi_Test_Environment.postman_environment.json`

---

## 🎯 Récapitulatif des Tâches

### Phase 1 : Préparation du Modèle

- [ ] Créer l'énumération `AuthProvider`
- [ ] Modifier l'entité `Utilisateur` (provider, providerId, enabled)
- [ ] Créer la migration Liquibase pour les nouveaux champs
- [ ] Mettre à jour les DTOs

### Phase 2 : Configuration OAuth2

- [ ] Ajouter les dépendances Maven OAuth2
- [ ] Configurer `application.yml` avec les providers
- [ ] Créer les classes OAuth2UserInfo pour chaque provider

### Phase 3 : Implémentation Sécurité

- [ ] Créer `OAuth2LoginSuccessHandler`
- [ ] Créer `CustomOAuth2UserService`
- [ ] Modifier `SecurityConfig` pour OAuth2
- [ ] Implémenter la génération JWT post-OAuth2

### Phase 4 : Services & Contrôleurs

- [ ] Créer/modifier le service utilisateur pour OAuth2
- [ ] Créer `OAuth2Controller`
- [ ] Adapter `AuthController`

### Phase 5 : Tests & Documentation

- [ ] Écrire les tests unitaires OAuth2
- [ ] Mettre à jour la documentation Swagger
- [ ] Mettre à jour les collections Postman

### Phase 6 : Déploiement

- [ ] Mettre à jour Docker Compose avec les variables OAuth2
- [ ] Configurer les secrets pour la production
- [ ] Tester le déploiement complet

---

## 🔗 Ressources

- [Spring Security OAuth2 Documentation](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html)
- [Google OAuth2 Setup](https://developers.google.com/identity/protocols/oauth2)
- [Facebook Login](https://developers.facebook.com/docs/facebook-login/)
- [Sign in with Apple](https://developer.apple.com/sign-in-with-apple/)
- [Okta Developer](https://developer.okta.com/)

---

*Document généré le 29/12/2024*
*Version: 1.0.0*

