package com.smartlogi.sdms.service.interfaces;

import com.smartlogi.sdms.dto.LivreurDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LivreurService {

    LivreurDTO save(@Valid LivreurDTO livreurDTO);

    LivreurDTO update(String id, @Valid LivreurDTO livreurDTO);

    LivreurDTO findById(String id);

    Page<LivreurDTO> findAll(Pageable pageable);

    void delete(String id);
}