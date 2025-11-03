package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.dto.ClientExpediteurDTO;
import com.smartlogi.sdms.entity.ClientExpediteur;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.mapper.ClientExpediteurMapper;
import com.smartlogi.sdms.repository.ClientExpediteurRepository;
import com.smartlogi.sdms.service.interfaces.ClientExpediteurService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClientExpediteurServiceImpl implements ClientExpediteurService {

    private static final Logger log = LoggerFactory.getLogger(ClientExpediteurServiceImpl.class);

    private final ClientExpediteurRepository clientExpediteurRepository;
    private final ClientExpediteurMapper clientExpediteurMapper;

    public ClientExpediteurServiceImpl(ClientExpediteurRepository clientExpediteurRepository,
                                       ClientExpediteurMapper clientExpediteurMapper) {
        this.clientExpediteurRepository = clientExpediteurRepository;
        this.clientExpediteurMapper = clientExpediteurMapper;
    }

    @Override
    public ClientExpediteurDTO save(ClientExpediteurDTO clientExpediteurDTO) {
        log.info("Création d'un nouveau client expéditeur : {}", clientExpediteurDTO.getEmail());
        ClientExpediteur client = clientExpediteurMapper.toEntity(clientExpediteurDTO);



        client = clientExpediteurRepository.save(client);
        return clientExpediteurMapper.toDto(client);
    }

    @Override
    public ClientExpediteurDTO update(String id, ClientExpediteurDTO clientExpediteurDTO) {
        log.info("Mise à jour du client expéditeur ID : {}", id);

        ClientExpediteur existingClient = clientExpediteurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ClientExpediteur non trouvé avec l'id : " + id));

        existingClient.setNom(clientExpediteurDTO.getNom());
        existingClient.setPrenom(clientExpediteurDTO.getPrenom());
        existingClient.setEmail(clientExpediteurDTO.getEmail());
        existingClient.setTelephone(clientExpediteurDTO.getTelephone());
        existingClient.setAdresse(clientExpediteurDTO.getAdresse());

        ClientExpediteur updatedClient = clientExpediteurRepository.save(existingClient);
        return clientExpediteurMapper.toDto(updatedClient);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientExpediteurDTO findById(String id) {
        log.debug("Recherche du client expéditeur ID : {}", id);
        return clientExpediteurRepository.findById(id)
                .map(clientExpediteurMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("ClientExpediteur non trouvé avec l'id : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientExpediteurDTO> findAll(Pageable pageable) {
        log.debug("Recherche de tous les clients expéditeurs (paginée)");
        return clientExpediteurRepository.findAll(pageable)
                .map(clientExpediteurMapper::toDto);
    }

    @Override
    public void delete(String id) {
        log.warn("Suppression du client expéditeur ID : {}", id);
        if (!clientExpediteurRepository.existsById(id)) {
            throw new ResourceNotFoundException("ClientExpediteur non trouvé avec l'id : " + id);
        }
        clientExpediteurRepository.deleteById(id);
    }
}