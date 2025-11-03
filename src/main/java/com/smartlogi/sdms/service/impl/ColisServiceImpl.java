package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.dto.ColisDTO;
import com.smartlogi.sdms.entity.Colis;
import com.smartlogi.sdms.entity.HistoriqueLivraison;
import com.smartlogi.sdms.entity.enumeration.Priorite;
import com.smartlogi.sdms.entity.enumeration.StatutColis;
import com.smartlogi.sdms.entity.Livreur;
import com.smartlogi.sdms.repository.LivreurRepository;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.exception.InvalidDataException;
import com.smartlogi.sdms.mapper.ColisMapper;
import com.smartlogi.sdms.repository.*;
import com.smartlogi.sdms.service.interfaces.ColisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest; // Important
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ColisServiceImpl implements ColisService {

    private static final Logger log = LoggerFactory.getLogger(ColisServiceImpl.class);

    private final ColisRepository colisRepository;
    private final ColisMapper colisMapper;
    private final HistoriqueLivraisonRepository historiqueLivraisonRepository;

    private final ClientExpediteurRepository clientExpediteurRepository;
    private final DestinataireRepository destinataireRepository;
    private final LivreurRepository livreurRepository;

    public ColisServiceImpl(ColisRepository colisRepository,
                            ColisMapper colisMapper,
                            HistoriqueLivraisonRepository historiqueLivraisonRepository,
                            ClientExpediteurRepository clientExpediteurRepository,
                            DestinataireRepository destinataireRepository,
                            LivreurRepository livreurRepository) {
        this.colisRepository = colisRepository;
        this.colisMapper = colisMapper;
        this.historiqueLivraisonRepository = historiqueLivraisonRepository;
        this.clientExpediteurRepository = clientExpediteurRepository;
        this.destinataireRepository = destinataireRepository;
        this.livreurRepository = livreurRepository;
    }


    @Override
    @Transactional
    public ColisDTO creerDemandeLivraison(ColisDTO colisDTO) {
        log.info("Début de la création de demande de livraison pour le client ID : {}", colisDTO.getClientExpediteurId());

        if (!clientExpediteurRepository.existsById(colisDTO.getClientExpediteurId())) {
            throw new ResourceNotFoundException("Client Expéditeur non trouvé avec l'id : " + colisDTO.getClientExpediteurId());
        }
        if (!destinataireRepository.existsById(colisDTO.getDestinataireId())) {
            throw new ResourceNotFoundException("Destinataire non trouvé avec l'id : " + colisDTO.getDestinataireId());
        }

        Colis colis = colisMapper.toEntity(colisDTO);

        colis.setStatut(StatutColis.CREE);

        colis.setDateCreation(LocalDateTime.now());
        colis.setDateDernierStatut(LocalDateTime.now());
        if (colis.getPriorite() == null) {
            colis.setPriorite(Priorite.NORMALE);
        }

        Colis savedColis = colisRepository.save(colis);
        log.info("Colis ID {} sauvegardé avec le statut CREE.", savedColis.getId());

        HistoriqueLivraison historique = new HistoriqueLivraison();
        historique.setColis(savedColis);
        historique.setStatut(StatutColis.CREE);
        historique.setCommentaire("Demande de livraison créée.");

        historiqueLivraisonRepository.save(historique);
        log.info("Entrée d'historique créée pour le colis ID {}.", savedColis.getId());

        return colisMapper.toDto(savedColis);
    }

    @Override
    public ColisDTO update(String id, ColisDTO colisDTO) {
        // Implémentation du CRUD de base (à venir)
        log.warn("Méthode update(ColisDTO) non encore implémentée.");
        throw new UnsupportedOperationException("Non implémenté");
    }

    @Override
    @Transactional(readOnly = true)
    public ColisDTO findById(String id) {
        log.debug("Recherche du colis ID : {}", id);
        return colisRepository.findById(id)
                .map(colisMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Colis non trouvé avec l'id : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ColisDTO> findAll(Pageable pageable) {
        log.debug("Recherche de tous les colis (paginée)");
        return colisRepository.findAll(pageable)
                .map(colisMapper::toDto);
    }

    @Override
    @Transactional
    public void delete(String id) {
        log.warn("Suppression du colis ID : {}", id);
        if (!colisRepository.existsById(id)) {
            throw new ResourceNotFoundException("Colis non trouvé avec l'id : " + id);
        }

        colisRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ColisDTO> findColisByClientExpediteur(String clientExpediteurId, Pageable pageable) {
        log.debug("Recherche des colis (paginée) pour le client ID : {}", clientExpediteurId);

        Page<Colis> colisPage = colisRepository.findAllByClientExpediteurId(clientExpediteurId, pageable);

        return colisPage.map(colisMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ColisDTO> findColisByDestinataire(String destinataireId, Pageable pageable) {
        log.debug("Recherche des colis (paginée) pour le destinataire ID : {}", destinataireId);

        Page<Colis> colisPage = colisRepository.findAllByDestinataireId(destinataireId, pageable);

        return colisPage.map(colisMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ColisDTO> findColisByLivreur(String livreurId, Pageable pageable) {
        log.debug("Recherche des colis (paginée) pour le livreur ID : {}", livreurId);

        Pageable pageableWithSort = pageable;

        if (pageable.getSort().isUnsorted()) {
            log.trace("Aucun tri spécifié. Application du tri par défaut (priorite DESC, zone.nom ASC)");


            Sort defaultSort = Sort.by(
                    Sort.Order.desc("priorite"),
                    Sort.Order.asc("zone.nom") // Tri par nom de la zone
            );

            pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
        }

        Page<Colis> colisPage = colisRepository.findAllByLivreurId(livreurId, pageableWithSort);

        return colisPage.map(colisMapper::toDto);
    }

    @Override
    @Transactional
    public ColisDTO updateStatutColis(String colisId, StatutColis newStatut, String commentaire) {
        log.info("Tentative de mise à jour du statut pour colis ID : {}. Nouveau statut : {}", colisId, newStatut);

        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new ResourceNotFoundException("Colis non trouvé avec l'id : " + colisId));

        StatutColis currentStatut = colis.getStatut();

        if (newStatut == null) {
            throw new InvalidDataException("Le nouveau statut ne peut pas être null.");
        }

        if (currentStatut == newStatut) {
            log.warn("Le colis {} est déjà au statut {}. Aucune action.", colisId, newStatut);
            return colisMapper.toDto(colis); // Retourne l'état actuel
        }

        if (currentStatut == StatutColis.LIVRE) {
            log.error("Transition de statut invalide : le colis {} est déjà LIVRE.", colisId);
            throw new InvalidDataException("Le statut d'un colis déjà livré ne peut pas être modifié.");
        }


        if (newStatut.ordinal() < currentStatut.ordinal()) {
            log.error("Transition de statut invalide (retour en arrière) : {} -> {} pour colis {}",
                    currentStatut, newStatut, colisId);
            throw new InvalidDataException("Transition de statut invalide (retour en arrière) : de " + currentStatut + " à " + newStatut);
        }

        colis.setStatut(newStatut);
        colis.setDateDernierStatut(LocalDateTime.now()); // Automatisé aussi par @PreUpdate

        Colis updatedColis = colisRepository.save(colis);

        HistoriqueLivraison historique = new HistoriqueLivraison();
        historique.setColis(updatedColis);
        historique.setStatut(newStatut);
        historique.setCommentaire(commentaire);

        historiqueLivraisonRepository.save(historique);
        log.info("Statut du colis ID {} mis à jour à {} et historique créé.", updatedColis.getId(), newStatut);

        return colisMapper.toDto(updatedColis);
    }

    @Override
    @Transactional
    public ColisDTO assignerColisLivreur(String colisId, String livreurId) {
        log.info("Tentative d'assignation du colis ID : {} au livreur ID : {}", colisId, livreurId);

        Colis colis = colisRepository.findById(colisId)
                .orElseThrow(() -> new ResourceNotFoundException("Colis non trouvé avec l'id : " + colisId));

        Livreur livreur = livreurRepository.findById(livreurId)
                .orElseThrow(() -> new ResourceNotFoundException("Livreur non trouvé avec l'id : " + livreurId));

        if (colis.getZone() != null && livreur.getZone() != null &&
                !colis.getZone().getId().equals(livreur.getZone().getId())) {

            log.warn("Assignation inter-zone : Le colis (Zone {}) est assigné au livreur (Zone {}).",
                    colis.getZone().getNom(), livreur.getZone().getNom());
        }

        colis.setLivreur(livreur);

        Colis updatedColis = colisRepository.save(colis);

        log.info("Colis ID {} assigné avec succès au livreur {}", colisId, livreur.getNom());
        return colisMapper.toDto(updatedColis);
    }
}