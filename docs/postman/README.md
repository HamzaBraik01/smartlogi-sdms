# 📘 Documentation Complete des Tests API SmartLogi

Ce guide explique comment tester les permissions et les endpoints de l'API SmartLogi avec Postman.

---

## 📑 Table des Matieres

1. [Fichiers Disponibles](#fichiers-disponibles)
2. [Prerequis](#prerequis)
3. [Donnees de Test (Base de Donnees)](#donnees-de-test-base-de-donnees)
4. [Collections Postman](#collections-postman)
5. [Plan de Test par Role](#plan-de-test-par-role)
6. [Tests de Securite](#tests-de-securite)
7. [Codes de Reponse HTTP](#codes-de-reponse-http)

---

## 📁 Fichiers Disponibles

```
docs/postman/
├── README.md                                          # Ce fichier
├── SmartLogi_Admin_API.postman_collection.json        # Collection Admin
├── SmartLogi_Manager_API.postman_collection.json      # Collection Manager
├── SmartLogi_Livreur_API.postman_collection.json      # Collection Livreur
├── SmartLogi_Client_API.postman_collection.json       # Collection Client
├── SmartLogi_Test_Environment.postman_environment.json # Environnement
└── test-data/
    └── 001-test-users-insert.sql                      # Script SQL
```

---

## 🔧 Prerequis

1. **Postman** installe sur votre machine
2. **L'application SmartLogi** demarree (`mvn spring-boot:run`)
3. **Base de donnees PostgreSQL** configuree et accessible

### Importer les Collections Postman

1. Ouvrez **Postman**
2. Cliquez sur **Import** (en haut a gauche)
3. Selectionnez tous les fichiers `.json` du dossier `docs/postman/`
4. Importez egalement l'environnement `SmartLogi_Test_Environment.postman_environment.json`
5. Selectionnez l'environnement dans le menu deroulant en haut a droite

---

## 🗄️ Donnees de Test (Base de Donnees)

### Insertion des Utilisateurs de Test

Executez le script SQL suivant dans votre base PostgreSQL :

```bash
psql -U smartlogi -d smartlogi_db -f docs/postman/test-data/001-test-users-insert.sql
```

### Comptes de Test Disponibles

| Role | Email | Mot de passe | ID |
|------|-------|--------------|-----|
| **ADMIN** | `admin@smartlogi.com` | `Admin@123` | `user-admin-001` |
| **MANAGER** | `manager@smartlogi.com` | `Admin@123` | `user-manager-001` |
| **LIVREUR 1** | `livreur1@smartlogi.com` | `Admin@123` | `user-livreur-001` |
| **LIVREUR 2** | `livreur2@smartlogi.com` | `Admin@123` | `user-livreur-002` |
| **CLIENT 1** | `client1@smartlogi.com` | `Admin@123` | `user-client-001` |
| **CLIENT 2** | `client2@smartlogi.com` | `Admin@123` | `user-client-002` |

### Zones de Test

| ID | Nom | Ville |
|----|-----|-------|
| `zone-casa-001` | Zone Casablanca Centre | Casablanca |
| `zone-rabat-001` | Zone Rabat Agdal | Rabat |

### Colis de Test

| ID | Client | Livreur | Statut |
|----|--------|---------|--------|
| `colis-test-001` | Client 1 | Livreur 1 | EN_ATTENTE |
| `colis-test-002` | Client 1 | Livreur 1 | EN_COURS |
| `colis-test-003` | Client 2 | Livreur 2 | EN_ATTENTE |
| `colis-test-004` | Client 2 | Livreur 2 | LIVRE |

---

## 📋 Collections Postman

### 1. SmartLogi_Admin_API.postman_collection.json

**Role teste:** ADMIN

| Dossier | Tests |
|---------|-------|
| Authentification | Login Admin, Manager, Client |
| Gestion des Roles | CRUD complet sur `/api/v1/admin/roles` |
| Gestion des Permissions | CRUD complet sur `/api/v1/admin/permissions` |
| Associations Role-Permission | Assignation/retrait de permissions |
| Tests Acces Refuse | Verification 401/403 |

### 2. SmartLogi_Manager_API.postman_collection.json

**Role teste:** MANAGER

| Dossier | Tests |
|---------|-------|
| Authentification | Login Manager |
| Gestion des Colis | CRUD sur `/api/v1/colis` → 200 |
| Gestion des Livreurs | CRUD sur `/api/v1/livreurs` → 200 |
| Gestion des Zones | CRUD sur `/api/v1/zones` → 200 |
| Gestion Logistique | Assignation, stats, recherche → 200 |
| Tests Acces Refuse | Admin endpoints → 403 |

### 3. SmartLogi_Livreur_API.postman_collection.json

**Role teste:** LIVREUR

| Dossier | Tests |
|---------|-------|
| Authentification | Login Livreur 1 & 2 |
| Acces Colis Assignes | Voir/modifier ses colis → 200 |
| Tests Acces Refuse | Colis autres livreurs → 403, Endpoints Manager → 403 |

### 4. SmartLogi_Client_API.postman_collection.json

**Role teste:** CLIENT

| Dossier | Tests |
|---------|-------|
| Authentification | Login Client 1 & 2 |
| Gestion des Colis | Creer/voir ses colis → 200/201 |
| Tests Acces Refuse | Colis autres clients → 403, Endpoints Manager → 403 |

---

## 🔒 Plan de Test par Role

### ADMIN - Acces Complet

```
✅ POST   /auth/login                     → 200 (token)
✅ GET    /api/v1/admin/roles             → 200
✅ POST   /api/v1/admin/roles             → 201
✅ PUT    /api/v1/admin/roles/{id}        → 200
✅ DELETE /api/v1/admin/roles/{id}        → 204
✅ GET    /api/v1/admin/permissions       → 200
✅ POST   /api/v1/admin/permissions       → 201
✅ Toutes les autres ressources           → 200
```

### MANAGER - Gestion Logistique

```
✅ POST   /auth/login                     → 200 (token)
✅ GET    /api/v1/colis                   → 200
✅ POST   /api/v1/colis                   → 201
✅ DELETE /api/v1/colis/{id}              → 204
✅ GET    /api/v1/livreurs                → 200
✅ POST   /api/v1/livreurs                → 201
✅ GET    /api/v1/zones                   → 200
✅ GET    /api/v1/gestion/statistiques    → 200
❌ GET    /api/v1/admin/roles             → 403
❌ POST   /api/v1/admin/roles             → 403
```

### LIVREUR - Acces Limite

```
✅ POST   /auth/login                          → 200 (token)
✅ GET    /api/v1/colis/livreur/{monId}        → 200
✅ GET    /api/v1/colis/{colisAssigne}         → 200
✅ PATCH  /api/v1/colis/{colisAssigne}/statut  → 200
❌ GET    /api/v1/colis/livreur/{autreId}      → 403
❌ PATCH  /api/v1/colis/{autreColisId}/statut  → 403
❌ GET    /api/v1/colis                        → 403
❌ POST   /api/v1/colis                        → 403
❌ GET    /api/v1/livreurs                     → 403
❌ GET    /api/v1/zones                        → 403
❌ GET    /api/v1/admin/roles                  → 403
```

### CLIENT - Expediteur

```
✅ POST   /auth/login                          → 200 (token)
✅ POST   /api/v1/colis                        → 201
✅ GET    /api/v1/colis/client/{monId}         → 200
✅ GET    /api/v1/colis/{monColis}             → 200
✅ GET    /api/v1/client-expediteurs/{monId}   → 200
✅ PUT    /api/v1/client-expediteurs/{monId}   → 200
❌ GET    /api/v1/colis/client/{autreId}       → 403
❌ GET    /api/v1/colis/{autreColisId}         → 403
❌ GET    /api/v1/colis                        → 403
❌ DELETE /api/v1/colis/{id}                   → 403
❌ PATCH  /api/v1/colis/{id}/statut            → 403
❌ GET    /api/v1/livreurs                     → 403
❌ GET    /api/v1/admin/roles                  → 403
```

---

## 🔐 Tests de Securite

### 1. Authentification (401)

| Test | Requete | Resultat Attendu |
|------|---------|------------------|
| Sans token | `GET /api/v1/colis` | 401 Unauthorized |
| Token invalide | `Bearer invalid.token` | 401 Unauthorized |
| Token expire | Token avec `exp` passe | 401 Unauthorized |
| Mauvais mot de passe | Login avec mauvais password | 401 Unauthorized |

### 2. Autorisation (403)

| Test | Requete | Resultat Attendu |
|------|---------|------------------|
| Client → Admin API | `GET /api/v1/admin/roles` | 403 Forbidden |
| Livreur → Manager API | `GET /api/v1/colis` | 403 Forbidden |
| Livreur1 → Colis Livreur2 | `GET /api/v1/colis/livreur/{livreur2}` | 403 Forbidden |
| Client1 → Colis Client2 | `GET /api/v1/colis/{colisClient2}` | 403 Forbidden |

### 3. Tests basés sur les Permissions

| Permission | Endpoint | Avec Permission | Sans Permission |
|------------|----------|-----------------|-----------------|
| `COLIS_CREATE` | POST /api/v1/colis | 201 | 403 |
| `COLIS_READ` | GET /api/v1/colis | 200 | 403 |
| `COLIS_DELETE` | DELETE /api/v1/colis/{id} | 204 | 403 |
| `LIVREUR_MANAGE` | GET /api/v1/livreurs | 200 | 403 |
| `ZONE_MANAGE` | GET /api/v1/zones | 200 | 403 |
| `STATS_VIEW` | GET /api/v1/gestion/statistiques | 200 | 403 |
| `ROLE_MANAGE` | GET /api/v1/admin/roles | 200 | 403 |

---

## 📊 Codes de Reponse HTTP

| Code | Signification | Quand |
|------|---------------|-------|
| **200** | OK | Requete reussie |
| **201** | Created | Ressource creee |
| **204** | No Content | Suppression reussie |
| **400** | Bad Request | Donnees invalides |
| **401** | Unauthorized | Token manquant/invalide |
| **403** | Forbidden | Pas les permissions |
| **404** | Not Found | Ressource inexistante |

---

## 🚀 Execution des Tests

### Executer une Collection Complete

1. Ouvrez Postman
2. Selectionnez l'environnement `SmartLogi Test Environment`
3. Cliquez sur les 3 points a cote de la collection
4. Selectionnez **Run collection**
5. Cliquez sur **Run**

### Executer avec Newman (CLI)

```bash
# Installer Newman
npm install -g newman

# Executer la collection Admin
newman run SmartLogi_Admin_API.postman_collection.json \
  -e SmartLogi_Test_Environment.postman_environment.json

# Executer la collection Manager
newman run SmartLogi_Manager_API.postman_collection.json \
  -e SmartLogi_Test_Environment.postman_environment.json

# Executer la collection Livreur
newman run SmartLogi_Livreur_API.postman_collection.json \
  -e SmartLogi_Test_Environment.postman_environment.json

# Executer la collection Client
newman run SmartLogi_Client_API.postman_collection.json \
  -e SmartLogi_Test_Environment.postman_environment.json
```

### Generer un Rapport HTML

```bash
npm install -g newman-reporter-htmlextra

newman run SmartLogi_Admin_API.postman_collection.json \
  -e SmartLogi_Test_Environment.postman_environment.json \
  -r htmlextra \
  --reporter-htmlextra-export ./reports/admin-report.html
```

---

## 📝 Resume des Permissions par Role

| Permission | ADMIN | MANAGER | LIVREUR | CLIENT |
|------------|:-----:|:-------:|:-------:|:------:|
| USER_MANAGE | ✅ | ❌ | ❌ | ❌ |
| ROLE_MANAGE | ✅ | ❌ | ❌ | ❌ |
| PERMISSION_MANAGE | ✅ | ❌ | ❌ | ❌ |
| COLIS_CREATE | ✅ | ✅ | ❌ | ✅ |
| COLIS_READ | ✅ | ✅ | ✅* | ✅* |
| COLIS_UPDATE | ✅ | ✅ | ❌ | ❌ |
| COLIS_DELETE | ✅ | ✅ | ❌ | ❌ |
| COLIS_UPDATE_STATUS | ✅ | ✅ | ✅* | ❌ |
| LIVREUR_MANAGE | ✅ | ✅ | ❌ | ❌ |
| ZONE_MANAGE | ✅ | ✅ | ❌ | ❌ |
| STATS_VIEW | ✅ | ✅ | ❌ | ❌ |

*\* Acces limite a ses propres ressources uniquement*

---

## 🔧 Variables d'Environnement

| Variable | Valeur | Description |
|----------|--------|-------------|
| `base_url` | `http://localhost:8080` | URL de l'API |
| `admin_token` | (genere) | Token JWT Admin |
| `manager_token` | (genere) | Token JWT Manager |
| `livreur1_token` | (genere) | Token JWT Livreur 1 |
| `client1_token` | (genere) | Token JWT Client 1 |
| `user_admin_id` | `user-admin-001` | ID Admin |
| `user_manager_id` | `user-manager-001` | ID Manager |
| `zone_casa_id` | `zone-casa-001` | ID Zone Casablanca |
| `colis_test_001` | `colis-test-001` | ID Colis Test 1 |

---

*Documentation generee le 2025-12-29*
