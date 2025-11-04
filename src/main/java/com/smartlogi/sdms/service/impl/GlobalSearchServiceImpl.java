package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.dto.GlobalSearchResponseDTO;
import com.smartlogi.sdms.entity.ClientExpediteur;
import com.smartlogi.sdms.entity.Colis;
import com.smartlogi.sdms.entity.Livreur;
import com.smartlogi.sdms.mapper.ClientExpediteurMapper;
import com.smartlogi.sdms.mapper.ColisMapper;
import com.smartlogi.sdms.mapper.LivreurMapper;
import com.smartlogi.sdms.repository.ClientExpediteurRepository;
import com.smartlogi.sdms.repository.ColisRepository;
import com.smartlogi.sdms.repository.LivreurRepository;
import com.smartlogi.sdms.service.interfaces.GlobalSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GlobalSearchServiceImpl implements GlobalSearchService {

    private static final Logger log = LoggerFactory.getLogger(GlobalSearchServiceImpl.class);

    private final ColisRepository colisRepository;
    private final ClientExpediteurRepository clientExpediteurRepository;
    private final LivreurRepository livreurRepository;

    private final ColisMapper colisMapper;
    private final ClientExpediteurMapper clientExpediteurMapper;
    private final LivreurMapper livreurMapper;

    public GlobalSearchServiceImpl(ColisRepository colisRepository,
                                   ClientExpediteurRepository clientExpediteurRepository,
                                   LivreurRepository livreurRepository,
                                   ColisMapper colisMapper,
                                   ClientExpediteurMapper clientExpediteurMapper,
                                   LivreurMapper livreurMapper) {
        this.colisRepository = colisRepository;
        this.clientExpediteurRepository = clientExpediteurRepository;
        this.livreurRepository = livreurRepository;
        this.colisMapper = colisMapper;
        this.clientExpediteurMapper = clientExpediteurMapper;
        this.livreurMapper = livreurMapper;
    }

    @Override
    public GlobalSearchResponseDTO rechercher(String motCle, Pageable pageable) {
        log.debug("Exécution de la recherche globale pour le mot-clé : {}", motCle);

        Page<Colis> colisPage = colisRepository.searchByKeyword(motCle, pageable);
        Page<ClientExpediteur> clientPage = clientExpediteurRepository.searchByKeyword(motCle, pageable);
        Page<Livreur> livreurPage = livreurRepository.searchByKeyword(motCle, pageable);

        GlobalSearchResponseDTO response = new GlobalSearchResponseDTO();
        response.setColis(colisPage.map(colisMapper::toDto));
        response.setClients(clientPage.map(clientExpediteurMapper::toDto));
        response.setLivreurs(livreurPage.map(livreurMapper::toDto));

        return response;
    }
}