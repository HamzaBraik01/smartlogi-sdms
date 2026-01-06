package com.smartlogi.sdms.integration;

import com.smartlogi.sdms.dto.ColisDTO;
import com.smartlogi.sdms.dto.ColisProduitDTO;
import com.smartlogi.sdms.entity.*;
import com.smartlogi.sdms.entity.enumeration.Priorite;
import com.smartlogi.sdms.entity.enumeration.StatutColis;
import com.smartlogi.sdms.repository.*;
import com.smartlogi.sdms.service.interfaces.ColisService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Tests d'Intégration - Cycle de vie complet d'un Colis")
class ColisLifecycleIntegrationTest {

    @Autowired
    private ColisService colisService;

    @Autowired
    private ColisRepository colisRepository;

    @Autowired
    private HistoriqueLivraisonRepository historiqueLivraisonRepository;

    @Autowired
    private ClientExpediteurRepository clientExpediteurRepository;

    @Autowired
    private DestinataireRepository destinataireRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private LivreurRepository livreurRepository;

    private static String colisId;
    private ClientExpediteur clientExpediteur;
    private Destinataire destinataire;
    private Produit produit;
    private Zone zone;
    private Livreur livreur;

    @BeforeEach
    void setUp() {
        // Nettoyer les données avant chaque test
        historiqueLivraisonRepository.deleteAll();
        colisRepository.deleteAll();
        livreurRepository.deleteAll();
        produitRepository.deleteAll();
        destinataireRepository.deleteAll();
        clientExpediteurRepository.deleteAll();
        zoneRepository.deleteAll();

        // Créer une zone
        zone = new Zone();
        zone.setNom("Zone Test");
        zone.setCodePostal("75001");
        zone.setVille("Paris");
        zone = zoneRepository.save(zone);

        // Créer un client expéditeur
        clientExpediteur = new ClientExpediteur();
        clientExpediteur.setNom("Doe");
        clientExpediteur.setPrenom("John");
        clientExpediteur.setEmail("john.doe@test.com");
        clientExpediteur.setTelephone("0612345678");
        clientExpediteur.setAdresse("123 Rue Test");
        clientExpediteur = clientExpediteurRepository.save(clientExpediteur);

        // Créer un destinataire
        destinataire = new Destinataire();
        destinataire.setNom("Martin");
        destinataire.setPrenom("Sophie");
        destinataire.setEmail("sophie.martin@test.com");
        destinataire.setTelephone("0687654321");
        destinataire.setAdresse("456 Avenue Test, Paris");
        destinataire = destinataireRepository.save(destinataire);

        // Créer un produit
        produit = new Produit();
        produit.setNom("Ordinateur Portable");
        produit.setPoids(2.5);
        produit.setPrix(new BigDecimal("999.99"));
        produit = produitRepository.save(produit);

        // Créer un livreur
        livreur = new Livreur();
        livreur.setNom("Dupont");
        livreur.setPrenom("Pierre");
        livreur.setEmail("pierre.dupont@test.com");
        livreur.setTelephone("0698765432");
        livreur.setVehicule("Moto");
        livreur.setZone(zone);
        livreur = livreurRepository.save(livreur);
    }

    @Test
    @Order(1)
    @DisplayName("1. Créer une demande de livraison et vérifier l'historique initial")
    @Transactional
    void testCreerDemandeLivraison_VerifierHistoriqueInitial() {
        // Given - Créer un DTO de colis avec un produit
        ColisProduitDTO colisProduitDTO = new ColisProduitDTO();
        colisProduitDTO.setProduitId(produit.getId());
        colisProduitDTO.setQuantite(2);

        ColisDTO colisDTO = new ColisDTO();
        colisDTO.setDescription("Colis contenant du matériel informatique");
        colisDTO.setPoidsTotal(5.0);
        colisDTO.setPriorite(Priorite.NORMALE);
        colisDTO.setVilleDestination("Paris");
        colisDTO.setClientExpediteurId(clientExpediteur.getId());
        colisDTO.setDestinataireId(destinataire.getId());
        colisDTO.setProduits(List.of(colisProduitDTO));

        // When - Créer la demande de livraison
        ColisDTO savedColisDTO = colisService.creerDemandeLivraison(colisDTO);
        colisId = savedColisDTO.getId();

        // Then - Vérifier que le colis est créé avec le bon statut initial
        assertThat(savedColisDTO).isNotNull();
        assertThat(savedColisDTO.getId()).isNotNull();
        assertThat(savedColisDTO.getStatut()).isEqualTo(StatutColis.CREE);
        assertThat(savedColisDTO.getDescription()).isEqualTo("Colis contenant du matériel informatique");
        assertThat(savedColisDTO.getPoidsTotal()).isEqualTo(5.0);
        assertThat(savedColisDTO.getPriorite()).isEqualTo(Priorite.NORMALE);

        // Vérifier en base de données
        Colis colisEnBase = colisRepository.findWithProduitsById(colisId).orElseThrow();
        assertThat(colisEnBase.getStatut()).isEqualTo(StatutColis.CREE);
        assertThat(colisEnBase.getClientExpediteur().getId()).isEqualTo(clientExpediteur.getId());
        assertThat(colisEnBase.getDestinataire().getId()).isEqualTo(destinataire.getId());

        // Forcer le chargement des ColisProduits dans la transaction
        int nbProduits = colisEnBase.getColisProduits().size();
        assertThat(nbProduits).isEqualTo(1);
        if (!colisEnBase.getColisProduits().isEmpty()) {
            assertThat(colisEnBase.getColisProduits().iterator().next().getQuantite()).isEqualTo(2);
        }

        // Vérifier l'historique - Un seul enregistrement pour la création
        List<HistoriqueLivraison> historique = historiqueLivraisonRepository.findAllByColisIdOrderByDateChangementDesc(colisId);
        assertThat(historique).hasSize(1);
        assertThat(historique.get(0).getStatut()).isEqualTo(StatutColis.CREE);
        assertThat(historique.get(0).getColis().getId()).isEqualTo(colisId);
    }

    @Test
    @Order(2)
    @DisplayName("2. Mettre à jour le statut vers COLLECTE et vérifier l'historique")
    @Transactional
    void testUpdateStatut_COLLECTE_VerifierHistorique() {
        // Given - Créer d'abord un colis
        ColisProduitDTO colisProduitDTO = new ColisProduitDTO();
        colisProduitDTO.setProduitId(produit.getId());
        colisProduitDTO.setQuantite(1);

        ColisDTO colisDTO = new ColisDTO();
        colisDTO.setDescription("Colis test pour collecte");
        colisDTO.setPoidsTotal(3.0);
        colisDTO.setPriorite(Priorite.HAUTE);
        colisDTO.setVilleDestination("Paris");
        colisDTO.setClientExpediteurId(clientExpediteur.getId());
        colisDTO.setDestinataireId(destinataire.getId());
        colisDTO.setProduits(List.of(colisProduitDTO));

        ColisDTO savedColisDTO = colisService.creerDemandeLivraison(colisDTO);
        String testColisId = savedColisDTO.getId();

        // When - Mettre à jour le statut vers COLLECTE
        String commentaire = "Colis collecté chez le client";
        ColisDTO updatedColisDTO = colisService.updateStatutColis(testColisId, StatutColis.COLLECTE, commentaire);

        // Then - Vérifier le nouveau statut
        assertThat(updatedColisDTO.getStatut()).isEqualTo(StatutColis.COLLECTE);

        // Vérifier en base de données
        Colis colisEnBase = colisRepository.findById(testColisId).orElseThrow();
        assertThat(colisEnBase.getStatut()).isEqualTo(StatutColis.COLLECTE);

        // Vérifier l'historique - Deux enregistrements (CREE + COLLECTE)
        List<HistoriqueLivraison> historique = historiqueLivraisonRepository.findAllByColisIdOrderByDateChangementDesc(testColisId);
        assertThat(historique).hasSize(2);

        // Le plus récent doit être COLLECTE
        assertThat(historique.get(0).getStatut()).isEqualTo(StatutColis.COLLECTE);
        assertThat(historique.get(0).getCommentaire()).isEqualTo(commentaire);

        // Le plus ancien doit être CREE
        assertThat(historique.get(1).getStatut()).isEqualTo(StatutColis.CREE);
    }

    @Test
    @Order(3)
    @DisplayName("3. Cycle complet : CREE -> COLLECTE -> EN_STOCK -> EN_TRANSIT -> LIVRE")
    @Transactional
    void testCycleComplet_VerifierToutesLesTransitions() {
        // Given - Créer un colis
        ColisProduitDTO colisProduitDTO = new ColisProduitDTO();
        colisProduitDTO.setProduitId(produit.getId());
        colisProduitDTO.setQuantite(3);

        ColisDTO colisDTO = new ColisDTO();
        colisDTO.setDescription("Colis pour cycle complet");
        colisDTO.setPoidsTotal(7.5);
        colisDTO.setPriorite(Priorite.URGENTE);
        colisDTO.setVilleDestination("Paris");
        colisDTO.setClientExpediteurId(clientExpediteur.getId());
        colisDTO.setDestinataireId(destinataire.getId());
        colisDTO.setProduits(List.of(colisProduitDTO));

        ColisDTO savedColisDTO = colisService.creerDemandeLivraison(colisDTO);
        String testColisId = savedColisDTO.getId();

        // Vérifier statut initial CREE
        assertThat(savedColisDTO.getStatut()).isEqualTo(StatutColis.CREE);

        // When & Then - Transition 1: CREE -> COLLECTE
        ColisDTO colisCollecte = colisService.updateStatutColis(testColisId, StatutColis.COLLECTE, "Colis récupéré");
        assertThat(colisCollecte.getStatut()).isEqualTo(StatutColis.COLLECTE);

        // Transition 2: COLLECTE -> EN_STOCK
        ColisDTO colisEnStock = colisService.updateStatutColis(testColisId, StatutColis.EN_STOCK, "Arrivé à l'entrepôt");
        assertThat(colisEnStock.getStatut()).isEqualTo(StatutColis.EN_STOCK);

        // Transition 3: EN_STOCK -> EN_TRANSIT
        ColisDTO colisEnTransit = colisService.updateStatutColis(testColisId, StatutColis.EN_TRANSIT, "Colis en cours de livraison");
        assertThat(colisEnTransit.getStatut()).isEqualTo(StatutColis.EN_TRANSIT);

        // Transition 4: EN_TRANSIT -> LIVRE
        ColisDTO colisLivre = colisService.updateStatutColis(testColisId, StatutColis.LIVRE, "Livré au destinataire");
        assertThat(colisLivre.getStatut()).isEqualTo(StatutColis.LIVRE);

        // Vérifier l'état final en base de données
        Colis colisEnBase = colisRepository.findById(testColisId).orElseThrow();
        assertThat(colisEnBase.getStatut()).isEqualTo(StatutColis.LIVRE);

        // Vérifier l'historique complet - 5 enregistrements
        List<HistoriqueLivraison> historique = historiqueLivraisonRepository.findAllByColisIdOrderByDateChangementDesc(testColisId);
        assertThat(historique).hasSize(5);

        // Extraire tous les statuts de l'historique
        List<StatutColis> statuts = historique.stream()
                .map(HistoriqueLivraison::getStatut)
                .toList();

        // Vérifier que tous les statuts attendus sont présents
        assertThat(statuts).containsExactlyInAnyOrder(
                StatutColis.CREE,
                StatutColis.COLLECTE,
                StatutColis.EN_STOCK,
                StatutColis.EN_TRANSIT,
                StatutColis.LIVRE
        );

        // Vérifier les commentaires par statut
        historique.forEach(h -> {
            switch (h.getStatut()) {
                case LIVRE -> assertThat(h.getCommentaire()).isEqualTo("Livré au destinataire");
                case EN_TRANSIT -> assertThat(h.getCommentaire()).isEqualTo("Colis en cours de livraison");
                case EN_STOCK -> assertThat(h.getCommentaire()).isEqualTo("Arrivé à l'entrepôt");
                case COLLECTE -> assertThat(h.getCommentaire()).isEqualTo("Colis récupéré");
                case CREE -> {} // Pas de commentaire spécifique pour CREE
                default -> {}
            }
        });
    }

    @Test
    @Order(4)
    @DisplayName("4. Vérifier l'assignation d'un livreur et le suivi")
    @Transactional
    void testAssignationLivreur_VerifierHistorique() {
        // Given - Créer un colis
        ColisProduitDTO colisProduitDTO = new ColisProduitDTO();
        colisProduitDTO.setProduitId(produit.getId());
        colisProduitDTO.setQuantite(1);

        ColisDTO colisDTO = new ColisDTO();
        colisDTO.setDescription("Colis pour test assignation");
        colisDTO.setPoidsTotal(2.0);
        colisDTO.setPriorite(Priorite.NORMALE);
        colisDTO.setVilleDestination("Paris");
        colisDTO.setClientExpediteurId(clientExpediteur.getId());
        colisDTO.setDestinataireId(destinataire.getId());
        colisDTO.setProduits(List.of(colisProduitDTO));

        ColisDTO savedColisDTO = colisService.creerDemandeLivraison(colisDTO);
        String testColisId = savedColisDTO.getId();

        // Passer le colis à EN_STOCK
        colisService.updateStatutColis(testColisId, StatutColis.COLLECTE, "Collecté");
        colisService.updateStatutColis(testColisId, StatutColis.EN_STOCK, "En stock");

        // When - Assigner le colis à un livreur
        ColisDTO colisAssigne = colisService.assignerColisLivreur(testColisId, livreur.getId());

        // Then - Vérifier l'assignation
        assertThat(colisAssigne.getLivreurId()).isEqualTo(livreur.getId());

        // Vérifier en base de données
        Colis colisEnBase = colisRepository.findById(testColisId).orElseThrow();
        assertThat(colisEnBase.getLivreur()).isNotNull();
        assertThat(colisEnBase.getLivreur().getId()).isEqualTo(livreur.getId());

        // Passer le colis à EN_TRANSIT puis LIVRE
        colisService.updateStatutColis(testColisId, StatutColis.EN_TRANSIT, "En route vers le destinataire");
        colisService.updateStatutColis(testColisId, StatutColis.LIVRE, "Livré avec succès");

        // Vérifier l'historique complet
        List<HistoriqueLivraison> historique = historiqueLivraisonRepository.findAllByColisIdOrderByDateChangementDesc(testColisId);
        assertThat(historique).hasSize(5); // CREE, COLLECTE, EN_STOCK, EN_TRANSIT, LIVRE

        // Vérifier que le dernier statut est LIVRE
        Colis colisFinale = colisRepository.findById(testColisId).orElseThrow();
        assertThat(colisFinale.getStatut()).isEqualTo(StatutColis.LIVRE);
        assertThat(colisFinale.getLivreur().getId()).isEqualTo(livreur.getId());
    }

    @Test
    @Order(5)
    @DisplayName("5. Vérifier la persistance des produits dans le colis")
    @Transactional
    void testPersistanceProduits_DansLeColis() {
        // Given - Créer un colis avec plusieurs produits
        ColisProduitDTO produit1 = new ColisProduitDTO();
        produit1.setProduitId(produit.getId());
        produit1.setQuantite(2);

        // Créer un deuxième produit
        Produit produit2Entity = new Produit();
        produit2Entity.setNom("Clavier");
        produit2Entity.setPoids(0.5);
        produit2Entity.setPrix(new BigDecimal("49.99"));
        produit2Entity = produitRepository.save(produit2Entity);

        ColisProduitDTO produit2 = new ColisProduitDTO();
        produit2.setProduitId(produit2Entity.getId());
        produit2.setQuantite(3);

        ColisDTO colisDTO = new ColisDTO();
        colisDTO.setDescription("Colis multi-produits");
        colisDTO.setPoidsTotal(6.5); // 2*2.5 + 3*0.5
        colisDTO.setPriorite(Priorite.NORMALE);
        colisDTO.setVilleDestination("Paris");
        colisDTO.setClientExpediteurId(clientExpediteur.getId());
        colisDTO.setDestinataireId(destinataire.getId());
        colisDTO.setProduits(List.of(produit1, produit2));

        // When - Créer le colis
        ColisDTO savedColisDTO = colisService.creerDemandeLivraison(colisDTO);

        // Then - Vérifier en base de données
        Colis colisEnBase = colisRepository.findWithProduitsById(savedColisDTO.getId()).orElseThrow();

        // Forcer le chargement de la collection dans la transaction
        int nbProduits = colisEnBase.getColisProduits().size();
        assertThat(nbProduits).isEqualTo(2);

        // Vérifier le premier produit
        ColisProduit cp1 = colisEnBase.getColisProduits().stream()
                .filter(cp -> cp.getProduit().getNom().equals("Ordinateur Portable"))
                .findFirst()
                .orElseThrow();
        assertThat(cp1.getQuantite()).isEqualTo(2);

        // Vérifier le deuxième produit
        ColisProduit cp2 = colisEnBase.getColisProduits().stream()
                .filter(cp -> cp.getProduit().getNom().equals("Clavier"))
                .findFirst()
                .orElseThrow();
        assertThat(cp2.getQuantite()).isEqualTo(3);

        // Vérifier l'historique
        List<HistoriqueLivraison> historique = historiqueLivraisonRepository.findAllByColisIdOrderByDateChangementDesc(savedColisDTO.getId());
        assertThat(historique).hasSize(1);
        assertThat(historique.get(0).getStatut()).isEqualTo(StatutColis.CREE);
    }

    @AfterEach
    void tearDown() {
        // Nettoyer après chaque test
        if (colisId != null) {
            historiqueLivraisonRepository.deleteAll();
            colisRepository.deleteAll();
        }
    }
}

