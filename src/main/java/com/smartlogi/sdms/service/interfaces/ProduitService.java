package com.smartlogi.sdms.service.interfaces;

import com.smartlogi.sdms.dto.ProduitDTO;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProduitService {

    ProduitDTO save(@Valid ProduitDTO produitDTO);

    ProduitDTO update(String id, @Valid ProduitDTO produitDTO);

    ProduitDTO findById(String id);

    Page<ProduitDTO> findAll(Pageable pageable);

    void delete(String id);
}