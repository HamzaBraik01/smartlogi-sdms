package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.dto.*;
import com.smartlogi.sdms.entity.*;
import com.smartlogi.sdms.entity.enumeration.Priorite;
import com.smartlogi.sdms.entity.enumeration.StatutColis;
import com.smartlogi.sdms.exception.InvalidDataException;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.mapper.ColisMapper;
import com.smartlogi.sdms.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitaires pour ColisServiceImpl")
class ColisServiceImplTest {


    @Mock private ColisRepository colisRepository;
    @Mock private ColisMapper colisMapper;
    @Mock private HistoriqueLivraisonRepository historiqueLivraisonRepository;
    @Mock private ClientExpediteurRepository clientExpediteurRepository;
    @Mock private DestinataireRepository destinataireRepository;
    @Mock private ProduitRepository produitRepository;
    @Mock private ColisProduitRepository colisProduitRepository;
    @Mock private LivreurRepository livreurRepository;



    @InjectMocks
    private ColisServiceImpl colisService;

    private ColisDTO colisDTO_in;
    private Colis colis_entity;
    private Produit produit_entity;

    @BeforeEach
    void setUp() {

        produit_entity = new Produit();
        produit_entity.setId("prod-1");
        produit_entity.setPoids(2.5);

        ColisProduitDTO produitDto = new ColisProduitDTO();
        produitDto.setProduitId("prod-1");
        produitDto.setQuantite(2);

        colisDTO_in = new ColisDTO();
        colisDTO_in.setClientExpediteurId("client-1");
        colisDTO_in.setDestinataireId("dest-1");
        colisDTO_in.setPriorite(Priorite.NORMALE);
        colisDTO_in.setProduits(List.of(produitDto));
        colisDTO_in.setDescription("Test colis");

        colis_entity = new Colis();
        colis_entity.setId("colis-1");
        ClientExpediteur client = new ClientExpediteur();
        client.setId("client-1");
        Destinataire destinataire = new Destinataire();
        destinataire.setId("dest-1");
        colis_entity.setClientExpediteur(client);
        colis_entity.setDestinataire(destinataire);
        colis_entity.setPriorite(Priorite.NORMALE);
    }


    @Test
    @DisplayName("doit créer un colis, calculer le poids et sauvegarder l'historique")
    void testCreerDemandeLivraison_CasNominal() {

        when(clientExpediteurRepository.existsById("client-1")).thenReturn(true);
        when(destinataireRepository.existsById("dest-1")).thenReturn(true);

        when(produitRepository.findById("prod-1")).thenReturn(Optional.of(produit_entity));

        when(colisMapper.toEntity(colisDTO_in)).thenReturn(colis_entity);


        when(colisRepository.save(colis_entity)).thenReturn(colis_entity);

        when(colisMapper.toDto(colis_entity)).thenReturn(colisDTO_in);

        ColisDTO resultatDTO = colisService.creerDemandeLivraison(colisDTO_in);


        assertNotNull(resultatDTO);
        assertThat(resultatDTO.getPriorite()).isEqualTo(Priorite.NORMALE);

        assertThat(colis_entity.getStatut()).isEqualTo(StatutColis.CREE);
        assertThat(colis_entity.getPoidsTotal()).isEqualTo(5.0); // 2.5 * 2


        verify(clientExpediteurRepository, times(1)).existsById("client-1");
        verify(destinataireRepository, times(1)).existsById("dest-1");

        // Note: colisProduitRepository.saveAll() n'est plus appelé car les ColisProduit
        // sont sauvegardés automatiquement via CascadeType.ALL sur la relation

        verify(colisRepository, times(2)).save(colis_entity);

        verify(historiqueLivraisonRepository, times(1)).save(any(HistoriqueLivraison.class));
    }


    @Test
    @DisplayName("doit mettre à jour le statut et créer un historique")
    void testUpdateStatutColis_CasNominal() {
        colis_entity.setStatut(StatutColis.CREE); // Statut initial

        when(colisRepository.findById("colis-1")).thenReturn(Optional.of(colis_entity));

        when(colisRepository.save(any(Colis.class))).thenReturn(colis_entity);

        when(colisMapper.toDto(colis_entity)).thenReturn(colisDTO_in);

        ArgumentCaptor<HistoriqueLivraison> historiqueCaptor = ArgumentCaptor.forClass(HistoriqueLivraison.class);

        colisService.updateStatutColis("colis-1", StatutColis.COLLECTE, "Pris en charge");


        assertThat(colis_entity.getStatut()).isEqualTo(StatutColis.COLLECTE);

        verify(historiqueLivraisonRepository).save(historiqueCaptor.capture());

        HistoriqueLivraison historiqueSauvegarde = historiqueCaptor.getValue();
        assertThat(historiqueSauvegarde.getStatut()).isEqualTo(StatutColis.COLLECTE);
        assertThat(historiqueSauvegarde.getCommentaire()).isEqualTo("Pris en charge");
    }


    @Test
    @DisplayName("doit jeter InvalidDataException si la transition de statut est invalide (ex: LIVRE -> EN_STOCK)")
    void testUpdateStatutColis_CasErreur_TransitionInvalide() {
        // --- Arrange (Given) ---
        colis_entity.setStatut(StatutColis.LIVRE); // Statut initial : déjà livré

        when(colisRepository.findById("colis-1")).thenReturn(Optional.of(colis_entity));


        InvalidDataException exception = assertThrows(InvalidDataException.class, () -> {
            colisService.updateStatutColis("colis-1", StatutColis.EN_STOCK, "Oups");
        });

        assertThat(exception.getMessage()).contains("déjà livré");

        verify(colisRepository, never()).save(any(Colis.class));
        verify(historiqueLivraisonRepository, never()).save(any(HistoriqueLivraison.class));
    }


    @Test
    @DisplayName("doit appeler le repository avec le tri par défaut (priorite DESC, zone.nom ASC)")
    void testFindColisByLivreur_VerifieTriParDefaut() {
        String livreurId = "livreur-1";
        Pageable pageableSansTri = PageRequest.of(0, 10);

        Sort triAttendu = Sort.by(
                Sort.Order.desc("priorite"),
                Sort.Order.asc("zone.nom")
        );

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        when(colisRepository.findAllByLivreurId(anyString(), any(Pageable.class)))
                .thenReturn(Page.empty()); // On se fiche du retour, on teste les paramètres

        colisService.findColisByLivreur(livreurId, pageableSansTri);


        verify(colisRepository).findAllByLivreurId(eq(livreurId), pageableCaptor.capture());

        Pageable pageableCapture = pageableCaptor.getValue();

        assertThat(pageableCapture.getSort()).isEqualTo(triAttendu);
        assertThat(pageableCapture.getPageNumber()).isEqualTo(0);
        assertThat(pageableCapture.getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("doit jeter UnsupportedOperationException pour la méthode update non implémentée")
    void testUpdate_MethodeNonImplementee() {
        assertThrows(UnsupportedOperationException.class, () -> {
            colisService.update("colis-1", new ColisDTO());
        });
    }
    @Test
    @DisplayName("doit retourner un ColisDTO quand l'ID est trouvé")
    void testFindById_CasNominal() {
        when(colisRepository.findById("colis-1")).thenReturn(Optional.of(colis_entity));
        when(colisMapper.toDto(colis_entity)).thenReturn(colisDTO_in);

        ColisDTO resultat = colisService.findById("colis-1");

        assertThat(resultat).isNotNull();
        assertThat(resultat).isEqualTo(colisDTO_in);
        verify(colisRepository).findById("colis-1");
    }
    @Test
    @DisplayName("doit jeter ResourceNotFoundException quand l'ID n'est pas trouvé")
    void testFindById_CasErreur_NonTrouve() {
        when(colisRepository.findById("bad-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            colisService.findById("bad-id");
        });
    }
    @Test
    @DisplayName("doit retourner une page de ColisDTO")
    void testFindAll_CasNominal() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Colis> pageEntites = Page.empty(pageable); // Un simple test

        when(colisRepository.findAll(pageable)).thenReturn(pageEntites);

        Page<ColisDTO> resultat = colisService.findAll(pageable);

        assertThat(resultat).isNotNull();
        verify(colisRepository).findAll(pageable);
    }

    @Test
    @DisplayName("doit appeler deleteById quand le colis existe")
    void testDelete_CasNominal() {
        when(colisRepository.existsById("colis-1")).thenReturn(true);
        doNothing().when(colisRepository).deleteById("colis-1");

        colisService.delete("colis-1");

        verify(colisRepository).existsById("colis-1");
        verify(colisRepository).deleteById("colis-1");
    }

    @Test
    @DisplayName("doit jeter ResourceNotFoundException si le colis à supprimer n'existe pas")
    void testDelete_CasErreur_NonTrouve() {
        when(colisRepository.existsById("bad-id")).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            colisService.delete("bad-id");
        });

        verify(colisRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("doit retourner les colis pour un client spécifique")
    void testFindColisByClientExpediteur() {
        Pageable pageable = PageRequest.of(0, 5);
        String clientId = "client-1";

        when(colisRepository.findAllByClientExpediteurId(clientId, pageable))
                .thenReturn(Page.empty(pageable));

        colisService.findColisByClientExpediteur(clientId, pageable);

        verify(colisRepository).findAllByClientExpediteurId(clientId, pageable);
    }

    @Test
    @DisplayName("doit retourner les colis pour un destinataire spécifique")
    void testFindColisByDestinataire() {
        Pageable pageable = PageRequest.of(0, 5);
        String destId = "dest-1";

        when(colisRepository.findAllByDestinataireId(destId, pageable))
                .thenReturn(Page.empty(pageable));

        colisService.findColisByDestinataire(destId, pageable);

        verify(colisRepository).findAllByDestinataireId(destId, pageable);
    }

    @Test
    @DisplayName("doit assigner un livreur à un colis")
    void testAssignerColisLivreur_CasNominal() {
        // --- Arrange (Given) ---
        Livreur livreur = new Livreur();
        livreur.setId("livreur-1");
        livreur.setNom("Youssef");

        when(colisRepository.findById("colis-1")).thenReturn(Optional.of(colis_entity));
        when(livreurRepository.findById("livreur-1")).thenReturn(Optional.of(livreur));
        when(colisRepository.save(any(Colis.class))).thenReturn(colis_entity);
        when(colisMapper.toDto(colis_entity)).thenReturn(colisDTO_in);

        colisService.assignerColisLivreur("colis-1", "livreur-1");


        assertThat(colis_entity.getLivreur()).isEqualTo(livreur);

        verify(colisRepository).save(colis_entity);
    }

    @Test
    @DisplayName("doit jeter ResourceNotFoundException si le livreur n'existe pas")
    void testAssignerColisLivreur_Erreur_LivreurNonTrouve() {

        when(colisRepository.findById("colis-1")).thenReturn(Optional.of(colis_entity));
        when(livreurRepository.findById("bad-livreur-id")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            colisService.assignerColisLivreur("colis-1", "bad-livreur-id");
        });

        verify(colisRepository, never()).save(any(Colis.class));
    }

    @Test
    @DisplayName("doit appeler le repository avec une Specification")
    void testFindAllColisByCriteria() {
        Pageable pageable = PageRequest.of(0, 10);
        when(colisRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(Page.empty(pageable));

        colisService.findAllColisByCriteria(StatutColis.EN_STOCK, "zone-1", "Tanger", Priorite.HAUTE, pageable);


        verify(colisRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("doit retourner les statistiques des repositories")
    void testGetStatistiquesTournees() {
        List<StatistiqueLivreurDTO> statsLivreurs = List.of();
        List<StatistiqueZoneDTO> statsZones = List.of();

        when(colisRepository.findStatistiquesParLivreur()).thenReturn(statsLivreurs);
        when(colisRepository.findStatistiquesParZone()).thenReturn(statsZones);

        StatistiquesTourneeDTO resultat = colisService.getStatistiquesTournees();

        assertThat(resultat).isNotNull();
        assertThat(resultat.getParLivreur()).isEqualTo(statsLivreurs);
        assertThat(resultat.getParZone()).isEqualTo(statsZones);
    }


}
