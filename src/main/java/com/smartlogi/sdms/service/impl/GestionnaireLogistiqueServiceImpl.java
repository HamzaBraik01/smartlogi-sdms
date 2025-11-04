package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.dto.GestionnaireLogistiqueDTO;
import com.smartlogi.sdms.dto.GlobalSearchResponseDTO;
import com.smartlogi.sdms.entity.GestionnaireLogistique;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.mapper.GestionnaireLogistiqueMapper;
import com.smartlogi.sdms.repository.GestionnaireLogistiqueRepository;
import com.smartlogi.sdms.service.interfaces.GestionnaireLogistiqueService;
import com.smartlogi.sdms.service.interfaces.GlobalSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GestionnaireLogistiqueServiceImpl implements GestionnaireLogistiqueService {

    private static final Logger log = LoggerFactory.getLogger(GestionnaireLogistiqueServiceImpl.class);

    private final GlobalSearchService globalSearchService;
    private final GestionnaireLogistiqueRepository gestionnaireRepository;
    private final GestionnaireLogistiqueMapper gestionnaireMapper;

    public GestionnaireLogistiqueServiceImpl(GlobalSearchService globalSearchService,
                                             GestionnaireLogistiqueRepository gestionnaireRepository,
                                             GestionnaireLogistiqueMapper gestionnaireMapper) {
        this.globalSearchService = globalSearchService;
        this.gestionnaireRepository = gestionnaireRepository;
        this.gestionnaireMapper = gestionnaireMapper;
    }


    @Override
    @Transactional(readOnly = true)
    public GlobalSearchResponseDTO rechercher(String motCle, Pageable pageable) {
        log.debug("Le gestionnaire exécute une recherche globale pour : {}", motCle);
        return globalSearchService.rechercher(motCle, pageable);
    }



    @Override
    public GestionnaireLogistiqueDTO save(GestionnaireLogistiqueDTO dto) {
        log.info("Création d'un gestionnaire : {}", dto.getEmail());
        GestionnaireLogistique entity = gestionnaireMapper.toEntity(dto);
        entity = gestionnaireRepository.save(entity);
        return gestionnaireMapper.toDto(entity);
    }

    @Override
    public GestionnaireLogistiqueDTO update(String id, GestionnaireLogistiqueDTO dto) {
        log.info("Mise à jour du gestionnaire ID : {}", id);
        GestionnaireLogistique existing = gestionnaireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gestionnaire non trouvé : " + id));

        existing.setNom(dto.getNom());
        existing.setPrenom(dto.getPrenom());
        existing.setEmail(dto.getEmail());
        existing.setTelephone(dto.getTelephone());

        existing = gestionnaireRepository.save(existing);
        return gestionnaireMapper.toDto(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public GestionnaireLogistiqueDTO findById(String id) {
        return gestionnaireRepository.findById(id)
                .map(gestionnaireMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Gestionnaire non trouvé : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GestionnaireLogistiqueDTO> findAll(Pageable pageable) {
        return gestionnaireRepository.findAll(pageable)
                .map(gestionnaireMapper::toDto);
    }

    @Override
    public void delete(String id) {
        log.warn("Suppression du gestionnaire ID : {}", id);
        if (!gestionnaireRepository.existsById(id)) {
            throw new ResourceNotFoundException("Gestionnaire non trouvé : " + id);
        }
        gestionnaireRepository.deleteById(id);
    }
}