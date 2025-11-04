package com.smartlogi.sdms.dto;
import lombok.Data;
import org.springframework.data.domain.Page;


@Data
public class GlobalSearchResponseDTO {
    private Page<ColisDTO> colis;
    private Page<ClientExpediteurDTO> clients;
    private Page<LivreurDTO> livreurs;
}
