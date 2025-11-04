package com.smartlogi.sdms.service.impl;
import com.smartlogi.sdms.dto.ZoneDTO;
import com.smartlogi.sdms.entity.Zone;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.mapper.ZoneMapper;
import com.smartlogi.sdms.repository.ZoneRepository;
import com.smartlogi.sdms.service.interfaces.ZoneService;
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
public class ZoneServiceImpl implements ZoneService {
    private static final Logger log = LoggerFactory.getLogger(ZoneServiceImpl.class);

    private final ZoneRepository zoneRepository;
    private final ZoneMapper zoneMapper;

    public ZoneServiceImpl(ZoneRepository zoneRepository, ZoneMapper zoneMapper) {
        this.zoneRepository = zoneRepository;
        this.zoneMapper = zoneMapper;
    }

    @Override
    public ZoneDTO save(ZoneDTO zoneDTO) {
        log.info("Création d'une nouvelle zone : {}", zoneDTO.getNom());
        Zone zone = zoneMapper.toEntity(zoneDTO);
        zone = zoneRepository.save(zone);
        return zoneMapper.toDto(zone);
    }

    @Override
    public ZoneDTO update(String id, ZoneDTO zoneDTO) {
        log.info("Mise à jour de la zone ID : {}", id);

        Zone existingZone = zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone non trouvée avec l'id : " + id));

        existingZone.setNom(zoneDTO.getNom());
        existingZone.setCodePostal(zoneDTO.getCodePostal());
        existingZone.setVille(zoneDTO.getVille());

        Zone updatedZone = zoneRepository.save(existingZone);
        return zoneMapper.toDto(updatedZone);
    }

    @Override
    @Transactional(readOnly = true)
    public ZoneDTO findById(String id) {
        log.debug("Recherche de la zone ID : {}", id);
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zone non trouvée avec l'id : " + id));
        return zoneMapper.toDto(zone);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ZoneDTO> findAll(Pageable pageable) {
        log.debug("Recherche de toutes les zones (paginée)");
        return zoneRepository.findAll(pageable)
                .map(zoneMapper::toDto);
    }

    @Override
    public void delete(String id) {
        log.warn("Suppression de la zone ID : {}", id);

        if (!zoneRepository.existsById(id)) {
            throw new ResourceNotFoundException("Zone non trouvée avec l'id : " + id);
        }



        zoneRepository.deleteById(id);
    }
}
