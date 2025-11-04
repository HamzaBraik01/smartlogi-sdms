package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.dto.HistoriqueLivraisonDTO;
import com.smartlogi.sdms.mapper.HistoriqueLivraisonMapper;
import com.smartlogi.sdms.repository.HistoriqueLivraisonRepository;
import com.smartlogi.sdms.service.interfaces.HistoriqueLivraisonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class HistoriqueLivraisonServiceImpl implements HistoriqueLivraisonService {

    private static final Logger log = LoggerFactory.getLogger(HistoriqueLivraisonServiceImpl.class);

    private final HistoriqueLivraisonRepository historiqueRepository;
    private final HistoriqueLivraisonMapper historiqueMapper;

    public HistoriqueLivraisonServiceImpl(HistoriqueLivraisonRepository historiqueRepository,
                                          HistoriqueLivraisonMapper historiqueMapper) {
        this.historiqueRepository = historiqueRepository;
        this.historiqueMapper = historiqueMapper;
    }


    @Override
    public List<HistoriqueLivraisonDTO> findHistoriqueByColisId(String colisId) {
        log.debug("Recherche de l'historique complet pour le colis ID : {}", colisId);

        return historiqueRepository.findAllByColisIdOrderByDateChangementDesc(colisId)
                .stream()
                .map(historiqueMapper::toDto)
                .collect(Collectors.toList());
    }
}