package com.smartlogi.sdms.service.interfaces;

import com.smartlogi.sdms.dto.ClientExpediteurDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientExpediteurService {

    ClientExpediteurDTO save(@Valid ClientExpediteurDTO clientExpediteurDTO);

    ClientExpediteurDTO update(String id, @Valid ClientExpediteurDTO clientExpediteurDTO);

    ClientExpediteurDTO findById(String id);

    Page<ClientExpediteurDTO> findAll(Pageable pageable);

    void delete(String id);
}