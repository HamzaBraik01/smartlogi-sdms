package com.smartlogi.sdms.service.impl;

import com.smartlogi.sdms.dto.ProduitDTO;
import com.smartlogi.sdms.entity.Produit;
import com.smartlogi.sdms.exception.ResourceNotFoundException;
import com.smartlogi.sdms.mapper.ProduitMapper;
import com.smartlogi.sdms.repository.ProduitRepository;
import com.smartlogi.sdms.service.interfaces.ProduitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProduitServiceImpl implements ProduitService {

    private static final Logger log = LoggerFactory.getLogger(ProduitServiceImpl.class);

    private final ProduitRepository produitRepository;
    private final ProduitMapper produitMapper;

    public ProduitServiceImpl(ProduitRepository produitRepository, ProduitMapper produitMapper) {
        this.produitRepository = produitRepository;
        this.produitMapper = produitMapper;
    }

    @Override
    public ProduitDTO save(ProduitDTO produitDTO) {
        log.info("Création d'un nouveau produit : {}", produitDTO.getNom());
        Produit produit = produitMapper.toEntity(produitDTO);
        produit = produitRepository.save(produit);
        return produitMapper.toDto(produit);
    }

    @Override
    public ProduitDTO update(String id, ProduitDTO produitDTO) {
        log.info("Mise à jour du produit ID : {}", id);

        Produit existingProduit = produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'id : " + id));

        existingProduit.setNom(produitDTO.getNom());
        existingProduit.setPoids(produitDTO.getPoids());
        existingProduit.setPrix(produitDTO.getPrix());

        Produit updatedProduit = produitRepository.save(existingProduit);
        return produitMapper.toDto(updatedProduit);
    }

    @Override
    @Transactional(readOnly = true)
    public ProduitDTO findById(String id) {
        log.debug("Recherche du produit ID : {}", id);
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'id : " + id));
        return produitMapper.toDto(produit);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProduitDTO> findAll(Pageable pageable) {
        log.debug("Recherche de tous les produits (paginée)");
        return produitRepository.findAll(pageable)
                .map(produitMapper::toDto);
    }

    @Override
    public void delete(String id) {
        log.warn("Suppression du produit ID : {}", id);

        if (!produitRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produit non trouvé avec l'id : " + id);
        }



        produitRepository.deleteById(id);
    }
}