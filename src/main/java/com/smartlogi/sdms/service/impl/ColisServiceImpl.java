package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.dto.ColisDTO;
import com.smartlogi.sdms.entity.Colis;
import com.smartlogi.sdms.entity.HistoriqueLivraison;
import com.smartlogi.sdms.entity.enumeration.Priorite;
import com.smartlogi.sdms.entity.enumeration.StatutColis;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.mapper.ColisMapper;
import com.smartlogi.sdms.repository.*; // Importer tous les repos
import com.smartlogi.sdms.service.interfaces.ColisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

    public ColisServiceImpl(ColisRepository colisRepository,
                            ColisMapper colisMapper,
                            HistoriqueLivraisonRepository historiqueLivraisonRepository,
                            ClientExpediteurRepository clientExpediteurRepository,
                            DestinataireRepository destinataireRepository) {
        this.colisRepository = colisRepository;
        this.colisMapper = colisMapper;
        this.historiqueLivraisonRepository = historiqueLivraisonRepository;
        this.clientExpediteurRepository = clientExpediteurRepository;
        this.destinataireRepository = destinataireRepository;
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
}