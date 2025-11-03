package com.smartlogi.sdms.service.interfaces;

import com.smartlogi.sdms.dto.LivreurDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LivreurService {

    LivreurDTO save(LivreurDTO livreurDTO);

    LivreurDTO update(String id, LivreurDTO livreurDTO);

    LivreurDTO findById(String id);

    Page<LivreurDTO> findAll(Pageable pageable);

    void delete(String id);
}