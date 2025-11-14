package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.dto.LivreurDTO;
import com.smartlogi.sdms.entity.Livreur;
import com.smartlogi.sdms.entity.Zone;
import com.smartlogi.sdms.exception.InvalidDataException;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.mapper.LivreurMapper;
import com.smartlogi.sdms.repository.LivreurRepository;
import com.smartlogi.sdms.repository.ZoneRepository; // Requis pour l'update
import com.smartlogi.sdms.service.interfaces.LivreurService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;


@Service
@Transactional
@Validated
public class LivreurServiceImpl implements LivreurService {

    private static final Logger log = LoggerFactory.getLogger(LivreurServiceImpl.class);

    private final LivreurRepository livreurRepository;
    private final LivreurMapper livreurMapper;
    private final ZoneRepository zoneRepository;

    public LivreurServiceImpl(LivreurRepository livreurRepository,
                              LivreurMapper livreurMapper,
                              ZoneRepository zoneRepository) {
        this.livreurRepository = livreurRepository;
        this.livreurMapper = livreurMapper;
        this.zoneRepository = zoneRepository;
    }

    @Override
    public LivreurDTO save(LivreurDTO livreurDTO) {
        log.info("Création d'un nouveau livreur : {}", livreurDTO.getEmail());

        // Vérifier si l'email existe déjà
        if (livreurRepository.findByEmail(livreurDTO.getEmail()).isPresent()) {
            throw new InvalidDataException("Un livreur avec cet email existe déjà : " + livreurDTO.getEmail());
        }

        Livreur livreur = livreurMapper.toEntity(livreurDTO);


        livreur = livreurRepository.save(livreur);
        return livreurMapper.toDto(livreur);
    }

    @Override
    public LivreurDTO update(String id, LivreurDTO livreurDTO) {
        log.info("Mise à jour du livreur ID : {}", id);

        Livreur existingLivreur = livreurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Livreur non trouvé avec l'id : " + id));

        existingLivreur.setNom(livreurDTO.getNom());
        existingLivreur.setPrenom(livreurDTO.getPrenom());
        existingLivreur.setEmail(livreurDTO.getEmail());
        existingLivreur.setTelephone(livreurDTO.getTelephone());
        existingLivreur.setVehicule(livreurDTO.getVehicule());

        if (livreurDTO.getZoneId() == null) {
            existingLivreur.setZone(null);
        } else {
            if (existingLivreur.getZone() == null || !livreurDTO.getZoneId().equals(existingLivreur.getZone().getId())) {
                log.debug("Assignation de la nouvelle zone {} au livreur {}", livreurDTO.getZoneId(), id);
                Zone zone = zoneRepository.findById(livreurDTO.getZoneId())
                        .orElseThrow(() -> new ResourceNotFoundException("Zone non trouvée pour l'assignation : " + livreurDTO.getZoneId()));
                existingLivreur.setZone(zone);
            }
        }

        Livreur updatedLivreur = livreurRepository.save(existingLivreur);
        return livreurMapper.toDto(updatedLivreur);
    }

    @Override
    @Transactional(readOnly = true)
    public LivreurDTO findById(String id) {
        log.debug("Recherche du livreur ID : {}", id);
        return livreurRepository.findById(id)
                .map(livreurMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Livreur non trouvé avec l'id : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LivreurDTO> findAll(Pageable pageable) {
        log.debug("Recherche de tous les livreurs (paginée)");
        return livreurRepository.findAll(pageable)
                .map(livreurMapper::toDto);
    }

    @Override
    public void delete(String id) {
        log.warn("Suppression du livreur ID : {}", id);
        if (!livreurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Livreur non trouvé avec l'id : " + id);
        }
        livreurRepository.deleteById(id);
    }
}