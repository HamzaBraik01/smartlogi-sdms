package com.smartlogi.sdms.service.interfaces;

import com.smartlogi.sdms.dto.ProduitDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProduitService {

    ProduitDTO save(ProduitDTO produitDTO);

    ProduitDTO update(String id, ProduitDTO produitDTO);

    ProduitDTO findById(String id);

    Page<ProduitDTO> findAll(Pageable pageable);

    void delete(String id);
}