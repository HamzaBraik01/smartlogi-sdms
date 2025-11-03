package com.smartlogi.sdms.service.interfaces;

import com.smartlogi.sdms.dto.ClientExpediteurDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientExpediteurService {

    ClientExpediteurDTO save(ClientExpediteurDTO clientExpediteurDTO);

    ClientExpediteurDTO update(String id, ClientExpediteurDTO clientExpediteurDTO);

    ClientExpediteurDTO findById(String id);

    Page<ClientExpediteurDTO> findAll(Pageable pageable);

    void delete(String id);
}