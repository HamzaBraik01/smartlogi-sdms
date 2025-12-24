-- =====================================================================
-- SMARTLOGI - Script SQL d'insertion des utilisateurs de test
-- =====================================================================
-- Auteur: SmartLogi QA Team
-- Date: 2025-12-29
-- Description: Donnees de test pour les collections Postman
-- =====================================================================

-- =====================================================================
-- MOTS DE PASSE DE TEST (pour reference - a utiliser dans Postman):
-- =====================================================================
-- TOUS LES UTILISATEURS: Admin@123
-- (Meme hash BCrypt pour tous: $2a$10$z0Ust6gwIUZrj/wKvTEED.Z4k7XyVFLLF8oTkSztczX2tojfVEc8q)
-- =====================================================================

-- =====================================================================
-- 1. ZONES DE LIVRAISON
-- =====================================================================
INSERT INTO zone (id, nom, code_postal, ville)
SELECT 'zone-casa-001', 'Zone Casablanca Centre', '20000', 'Casablanca'
WHERE NOT EXISTS (SELECT 1 FROM zone WHERE id = 'zone-casa-001');

INSERT INTO zone (id, nom, code_postal, ville)
SELECT 'zone-casa-002', 'Zone Casablanca Anfa', '20050', 'Casablanca'
WHERE NOT EXISTS (SELECT 1 FROM zone WHERE id = 'zone-casa-002');

INSERT INTO zone (id, nom, code_postal, ville)
SELECT 'zone-rabat-001', 'Zone Rabat Agdal', '10090', 'Rabat'
WHERE NOT EXISTS (SELECT 1 FROM zone WHERE id = 'zone-rabat-001');

INSERT INTO zone (id, nom, code_postal, ville)
SELECT 'zone-marrakech-001', 'Zone Marrakech Gueliz', '40000', 'Marrakech'
WHERE NOT EXISTS (SELECT 1 FROM zone WHERE id = 'zone-marrakech-001');

-- =====================================================================
-- 2. UTILISATEUR MANAGER
-- Email: manager@smartlogi.com | Password: Admin@123
-- =====================================================================
INSERT INTO utilisateur (id, nom, prenom, email, telephone, password, role, adresse)
SELECT 'user-manager-001', 'Benali', 'Youssef', 'manager@smartlogi.com', '+212600000002',
       '$2a$10$z0Ust6gwIUZrj/wKvTEED.Z4k7XyVFLLF8oTkSztczX2tojfVEc8q', 'MANAGER',
       '123 Avenue Mohammed V, Casablanca'
WHERE NOT EXISTS (SELECT 1 FROM utilisateur WHERE email = 'manager@smartlogi.com');

-- =====================================================================
-- 3. UTILISATEURS LIVREURS
-- Email: livreur1@smartlogi.com | Password: Admin@123
-- Email: livreur2@smartlogi.com | Password: Admin@123
-- =====================================================================
INSERT INTO utilisateur (id, nom, prenom, email, telephone, password, role, adresse, vehicule, zone_id)
SELECT 'user-livreur-001', 'Chakir', 'Ahmed', 'livreur1@smartlogi.com', '+212600000003',
       '$2a$10$z0Ust6gwIUZrj/wKvTEED.Z4k7XyVFLLF8oTkSztczX2tojfVEc8q', 'LIVREUR',
       '45 Rue Ibn Sina, Casablanca', 'Scooter Honda PCX 125', 'zone-casa-001'
WHERE NOT EXISTS (SELECT 1 FROM utilisateur WHERE email = 'livreur1@smartlogi.com');

INSERT INTO utilisateur (id, nom, prenom, email, telephone, password, role, adresse, vehicule, zone_id)
SELECT 'user-livreur-002', 'El Amrani', 'Karim', 'livreur2@smartlogi.com', '+212600000004',
       '$2a$10$z0Ust6gwIUZrj/wKvTEED.Z4k7XyVFLLF8oTkSztczX2tojfVEc8q', 'LIVREUR',
       '78 Boulevard Zerktouni, Rabat', 'Moto Yamaha NMAX', 'zone-rabat-001'
WHERE NOT EXISTS (SELECT 1 FROM utilisateur WHERE email = 'livreur2@smartlogi.com');

-- =====================================================================
-- 4. UTILISATEURS CLIENTS
-- Email: client1@smartlogi.com | Password: Admin@123
-- Email: client2@smartlogi.com | Password: Admin@123
-- =====================================================================
INSERT INTO utilisateur (id, nom, prenom, email, telephone, password, role, adresse)
SELECT 'user-client-001', 'Alaoui', 'Fatima', 'client1@smartlogi.com', '+212600000005',
       '$2a$10$z0Ust6gwIUZrj/wKvTEED.Z4k7XyVFLLF8oTkSztczX2tojfVEc8q', 'CLIENT',
       '12 Rue Hassan II, Casablanca'
WHERE NOT EXISTS (SELECT 1 FROM utilisateur WHERE email = 'client1@smartlogi.com');

INSERT INTO utilisateur (id, nom, prenom, email, telephone, password, role, adresse)
SELECT 'user-client-002', 'Bouaziz', 'Mohammed', 'client2@smartlogi.com', '+212600000006',
       '$2a$10$z0Ust6gwIUZrj/wKvTEED.Z4k7XyVFLLF8oTkSztczX2tojfVEc8q', 'CLIENT',
       '56 Avenue Allal Ben Abdellah, Rabat'
WHERE NOT EXISTS (SELECT 1 FROM utilisateur WHERE email = 'client2@smartlogi.com');

-- =====================================================================
-- 5. COLIS DE TEST
-- =====================================================================
-- Colis 1: Client 1 vers Livreur 1 (EN_ATTENTE)
INSERT INTO colis (id, description, poids_total, statut, priorite, ville_destination,
                   date_creation, client_expediteur_id, livreur_id, zone_id)
SELECT 'colis-test-001', 'Colis test Client 1 - Livraison 1', 2.5, 'EN_ATTENTE', 'NORMALE',
       'Casablanca', CURRENT_TIMESTAMP, 'user-client-001', 'user-livreur-001', 'zone-casa-001'
WHERE NOT EXISTS (SELECT 1 FROM colis WHERE id = 'colis-test-001')
  AND EXISTS (SELECT 1 FROM utilisateur WHERE id = 'user-client-001');

-- Colis 2: Client 1 vers Livreur 1 (EN_COURS)
INSERT INTO colis (id, description, poids_total, statut, priorite, ville_destination,
                   date_creation, client_expediteur_id, livreur_id, zone_id)
SELECT 'colis-test-002', 'Colis test Client 1 - En cours', 1.8, 'EN_COURS', 'HAUTE',
       'Casablanca', CURRENT_TIMESTAMP, 'user-client-001', 'user-livreur-001', 'zone-casa-001'
WHERE NOT EXISTS (SELECT 1 FROM colis WHERE id = 'colis-test-002')
  AND EXISTS (SELECT 1 FROM utilisateur WHERE id = 'user-client-001');

-- Colis 3: Client 2 vers Livreur 2 (EN_ATTENTE)
INSERT INTO colis (id, description, poids_total, statut, priorite, ville_destination,
                   date_creation, client_expediteur_id, livreur_id, zone_id)
SELECT 'colis-test-003', 'Colis test Client 2 - Livraison 1', 3.2, 'EN_ATTENTE', 'NORMALE',
       'Rabat', CURRENT_TIMESTAMP, 'user-client-002', 'user-livreur-002', 'zone-rabat-001'
WHERE NOT EXISTS (SELECT 1 FROM colis WHERE id = 'colis-test-003')
  AND EXISTS (SELECT 1 FROM utilisateur WHERE id = 'user-client-002');

-- Colis 4: Client 2 vers Livreur 2 (LIVRE)
INSERT INTO colis (id, description, poids_total, statut, priorite, ville_destination,
                   date_creation, client_expediteur_id, livreur_id, zone_id)
SELECT 'colis-test-004', 'Colis test Client 2 - Livre', 0.5, 'LIVRE', 'BASSE',
       'Rabat', CURRENT_TIMESTAMP, 'user-client-002', 'user-livreur-002', 'zone-rabat-001'
WHERE NOT EXISTS (SELECT 1 FROM colis WHERE id = 'colis-test-004')
  AND EXISTS (SELECT 1 FROM utilisateur WHERE id = 'user-client-002');

-- =====================================================================
-- RESUME DES COMPTES DE TEST
-- =====================================================================
/*
+------------------+---------------------------+---------------+------------------+
| Role             | Email                     | Password      | ID               |
+------------------+---------------------------+---------------+------------------+
| ADMIN            | admin@smartlogi.com       | Admin@123     | (existant)       |
| MANAGER          | manager@smartlogi.com     | Admin@123     | user-manager-001 |
| LIVREUR (1)      | livreur1@smartlogi.com    | Admin@123     | user-livreur-001 |
| LIVREUR (2)      | livreur2@smartlogi.com    | Admin@123     | user-livreur-002 |
| CLIENT (1)       | client1@smartlogi.com     | Admin@123     | user-client-001  |
| CLIENT (2)       | client2@smartlogi.com     | Admin@123     | user-client-002  |
+------------------+---------------------------+---------------+------------------+
*/

