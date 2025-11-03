package com.smartlogi.sdms.service.interfaces;
import com.smartlogi.sdms.dto.ZoneDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ZoneService {

    ZoneDTO save(ZoneDTO zoneDTO);


    ZoneDTO update(String id, ZoneDTO zoneDTO);


    ZoneDTO findById(String id);


    Page<ZoneDTO> findAll(Pageable pageable);


    void delete(String id);
}
