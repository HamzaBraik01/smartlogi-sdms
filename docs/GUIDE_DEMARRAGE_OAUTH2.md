# 🚀 Guide de Démarrage - Authentification OAuth2

## Configuration Rapide

### 1. Variables d'Environnement

Créez un fichier `.env` à la racine du projet (copiez `.env.example`) :

```bash
# Google OAuth2
GOOGLE_CLIENT_ID=votre-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=votre-client-secret

# Facebook OAuth2
FACEBOOK_CLIENT_ID=votre-app-id
FACEBOOK_CLIENT_SECRET=votre-app-secret

# Apple Sign In (optionnel)
APPLE_CLIENT_ID=votre-service-id
APPLE_CLIENT_SECRET=votre-client-secret

# Okta (optionnel)
OKTA_CLIENT_ID=votre-client-id
OKTA_CLIENT_SECRET=votre-client-secret
OKTA_ISSUER_URI=https://votre-domaine.okta.com/oauth2/default

# URI de redirection frontend
OAUTH2_REDIRECT_URI=http://localhost:4200/oauth2/redirect
```

### 2. Configuration des Providers OAuth2

#### Google Cloud Console

1. Allez sur [Google Cloud Console](https://console.cloud.google.com/apis/credentials)
2. Créez un projet ou sélectionnez-en un
3. Créez des identifiants OAuth 2.0
4. Ajoutez les URIs de redirection autorisées :
   - `http://localhost:8080/login/oauth2/code/google`
   - `https://votre-domaine.com/login/oauth2/code/google`

#### Facebook Developers

1. Allez sur [Facebook Developers](https://developers.facebook.com/apps)
2. Créez une application
3. Ajoutez le produit "Facebook Login"
4. Configurez les URIs OAuth :
   - `http://localhost:8080/login/oauth2/code/facebook`

#### Apple Developer (optionnel)

1. Allez sur [Apple Developer](https://developer.apple.com/account/resources/identifiers)
2. Créez un Service ID
3. Configurez Sign in with Apple

### 3. Lancement

```bash
# Développement local
mvn spring-boot:run

# Avec Docker
docker-compose -f docker-compose.app.yml up -d
```

---

## 📡 Endpoints Disponibles

### Authentification Classique

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/auth/login` | Connexion email/password |
| `POST` | `/auth/register` | Inscription |
| `GET` | `/auth/me` | Profil utilisateur |

### OAuth2

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `GET` | `/oauth2/authorization/google` | Login Google |
| `GET` | `/oauth2/authorization/facebook` | Login Facebook |
| `GET` | `/oauth2/authorization/apple` | Login Apple |
| `GET` | `/oauth2/authorization/okta` | Login Okta |
| `GET` | `/oauth2/providers` | Liste des providers |
| `GET` | `/oauth2/info` | Infos sur OAuth2 |

---

## 🔄 Flux d'Authentification

### Frontend Angular/React

```typescript
// 1. Rediriger vers OAuth2
window.location.href = 'http://localhost:8080/oauth2/authorization/google';

// 2. Après callback, récupérer le token depuis l'URL
// URL: http://localhost:4200/oauth2/redirect?token=xxx&userId=xxx&email=xxx
const params = new URLSearchParams(window.location.search);
const token = params.get('token');
const userId = params.get('userId');

// 3. Stocker le token
localStorage.setItem('auth_token', token);

// 4. Utiliser le token pour les requêtes API
fetch('/api/v1/colis', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
```

### Composant Angular

```typescript
// oauth2-redirect.component.ts
@Component({...})
export class OAuth2RedirectComponent implements OnInit {
  
  ngOnInit() {
    const params = new URLSearchParams(window.location.search);
    
    const token = params.get('token');
    const error = params.get('error');
    
    if (error) {
      console.error('Erreur OAuth2:', error);
      this.router.navigate(['/login']);
      return;
    }
    
    if (token) {
      this.authService.setToken(token);
      this.router.navigate(['/dashboard']);
    }
  }
}
```

---

## 🔒 Structure du JWT

Le token JWT généré (identique pour auth classique et OAuth2) :

```json
{
  "sub": "user@email.com",
  "userId": "uuid-de-l-utilisateur",
  "roles": "ROLE_CLIENT",
  "nom": "Doe",
  "prenom": "John",
  "provider": "GOOGLE",
  "iat": 1703826000,
  "exp": 1703912400
}
```

---

## 🧪 Test avec cURL

```bash
# 1. Liste des providers
curl http://localhost:8080/oauth2/providers

# 2. Après authentification OAuth2, utiliser le token
curl -H "Authorization: Bearer <votre-token>" \
     http://localhost:8080/auth/me

# 3. Accéder aux colis
curl -H "Authorization: Bearer <votre-token>" \
     http://localhost:8080/api/v1/colis
```

---

## 📝 Rôles par Défaut

| Mode d'authentification | Rôle par défaut |
|------------------------|-----------------|
| Inscription classique | Défini à l'inscription |
| OAuth2 (nouveau compte) | `CLIENT_EXPEDITEUR` |
| OAuth2 (compte existant) | Rôle conservé |

Un administrateur peut modifier le rôle via l'API admin.

---

## 🐳 Docker Compose

```bash
# Démarrer tous les services
docker-compose -f docker-compose.app.yml up -d

# Voir les logs
docker-compose -f docker-compose.app.yml logs -f sdms-api

# Arrêter
docker-compose -f docker-compose.app.yml down
```

---

## ⚠️ Dépannage

### Erreur "redirect_uri_mismatch"

Vérifiez que l'URI de callback est bien configurée chez le provider :
- `http://localhost:8080/login/oauth2/code/google`

### Erreur "invalid_client"

Vérifiez les variables d'environnement `CLIENT_ID` et `CLIENT_SECRET`.

### Token non reçu après redirection

Vérifiez que `OAUTH2_REDIRECT_URI` pointe vers votre frontend.

---

*Document créé le 29/12/2024*

