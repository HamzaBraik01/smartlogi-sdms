package com.smartlogi.sdms.service.interfaces;

import com.smartlogi.sdms.dto.DestinataireDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DestinataireService {

    DestinataireDTO save(@Valid DestinataireDTO destinataireDTO);

    DestinataireDTO update(String id, @Valid DestinataireDTO destinataireDTO);

    DestinataireDTO findById(String id);

    Page<DestinataireDTO> findAll(Pageable pageable);

    void delete(String id);
}