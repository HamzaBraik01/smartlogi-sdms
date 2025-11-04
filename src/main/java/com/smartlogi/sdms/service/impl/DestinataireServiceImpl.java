package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.dto.DestinataireDTO;
import com.smartlogi.sdms.entity.Destinataire;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.mapper.DestinataireMapper;
import com.smartlogi.sdms.repository.DestinataireRepository;
import com.smartlogi.sdms.service.interfaces.DestinataireService;
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
public class DestinataireServiceImpl implements DestinataireService {

    private static final Logger log = LoggerFactory.getLogger(DestinataireServiceImpl.class);

    private final DestinataireRepository destinataireRepository;
    private final DestinataireMapper destinataireMapper;

    public DestinataireServiceImpl(DestinataireRepository destinataireRepository,
                                   DestinataireMapper destinataireMapper) {
        this.destinataireRepository = destinataireRepository;
        this.destinataireMapper = destinataireMapper;
    }

    @Override
    public DestinataireDTO save(DestinataireDTO destinataireDTO) {
        log.info("Création d'un nouveau destinataire : {}", destinataireDTO.getEmail());
        Destinataire dest = destinataireMapper.toEntity(destinataireDTO);
        // (Voir note sécurité sur le mot de passe dans ClientExpediteurServiceImpl)
        dest = destinataireRepository.save(dest);
        return destinataireMapper.toDto(dest);
    }

    @Override
    public DestinataireDTO update(String id, DestinataireDTO destinataireDTO) {
        log.info("Mise à jour du destinataire ID : {}", id);

        Destinataire existingDest = destinataireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Destinataire non trouvé avec l'id : " + id));

        existingDest.setNom(destinataireDTO.getNom());
        existingDest.setPrenom(destinataireDTO.getPrenom());
        existingDest.setEmail(destinataireDTO.getEmail());
        existingDest.setTelephone(destinataireDTO.getTelephone());
        existingDest.setAdresse(destinataireDTO.getAdresse());

        Destinataire updatedDest = destinataireRepository.save(existingDest);
        return destinataireMapper.toDto(updatedDest);
    }

    @Override
    @Transactional(readOnly = true)
    public DestinataireDTO findById(String id) {
        log.debug("Recherche du destinataire ID : {}", id);
        return destinataireRepository.findById(id)
                .map(destinataireMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Destinataire non trouvé avec l'id : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DestinataireDTO> findAll(Pageable pageable) {
        log.debug("Recherche de tous les destinataires (paginée)");
        return destinataireRepository.findAll(pageable)
                .map(destinataireMapper::toDto);
    }

    @Override
    public void delete(String id) {
        log.warn("Suppression du destinataire ID : {}", id);
        if (!destinataireRepository.existsById(id)) {
            throw new ResourceNotFoundException("Destinataire non trouvé avec l'id : " + id);
        }
        destinataireRepository.deleteById(id);
    }
}