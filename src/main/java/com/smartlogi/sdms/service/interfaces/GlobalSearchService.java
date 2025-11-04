package com.smartlogi.sdms.service.interfaces;

import com.smartlogi.sdms.dto.GlobalSearchResponseDTO;
import org.springframework.data.domain.Pageable;

public interface GlobalSearchService {


    GlobalSearchResponseDTO rechercher(String motCle, Pageable pageable);
}