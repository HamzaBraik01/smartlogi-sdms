package com.smartlogi.sdms.mapper;

import com.smartlogi.sdms.dto.ColisDTO;
import com.smartlogi.sdms.dto.ColisProduitDTO;
import com.smartlogi.sdms.entity.*;
import com.smartlogi.sdms.entity.enumeration.Priorite;
import com.smartlogi.sdms.entity.enumeration.StatutColis;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Tests Unitaires pour ColisMapper")
class ColisMapperTest {

    @Autowired
    private ColisMapper mapper;

    @Test
    @DisplayName("doit mapper les ID (Strings) du DTO vers des entités imbriquées (toEntity)")
    void testToEntity_RelationMapping() {
        ColisDTO dto = new ColisDTO();
        dto.setClientExpediteurId("client-1");
        dto.setDestinataireId("dest-1");
        dto.setLivreurId("livreur-1");
        dto.setZoneId("zone-1");

        Colis entity = mapper.toEntity(dto);


        assertThat(entity).isNotNull();

        assertThat(entity.getClientExpediteur()).isNotNull();
        assertThat(entity.getClientExpediteur().getId()).isEqualTo("client-1");

        assertThat(entity.getDestinataire()).isNotNull();
        assertThat(entity.getDestinataire().getId()).isEqualTo("dest-1");

        assertThat(entity.getLivreur()).isNotNull();
        assertThat(entity.getLivreur().getId()).isEqualTo("livreur-1");

        assertThat(entity.getZone()).isNotNull();
        assertThat(entity.getZone().getId()).isEqualTo("zone-1");
    }

    @Test
    @DisplayName("doit aplatir les entités imbriquées vers des ID (Strings) (toDto)")
    void testToDto_RelationMapping() {
        Colis entity = new Colis();

        ClientExpediteur client = new ClientExpediteur();
        client.setId("client-1");
        entity.setClientExpediteur(client);

        Destinataire dest = new Destinataire();
        dest.setId("dest-1");
        entity.setDestinataire(dest);

        Livreur livreur = new Livreur();
        livreur.setId("livreur-1");
        entity.setLivreur(livreur);

        Zone zone = new Zone();
        zone.setId("zone-1");
        entity.setZone(zone);

        ColisDTO dto = mapper.toDto(entity);


        assertThat(dto).isNotNull();
        assertThat(dto.getClientExpediteurId()).isEqualTo("client-1");
        assertThat(dto.getDestinataireId()).isEqualTo("dest-1");
        assertThat(dto.getLivreurId()).isEqualTo("livreur-1");
        assertThat(dto.getZoneId()).isEqualTo("zone-1");
    }

    @Test
    @DisplayName("doit gérer les relations nulles dans toDto")
    void testToDto_NullRelations() {
        Colis entity = new Colis();
        entity.setId("colis-1");
        entity.setDescription("Test");
        entity.setClientExpediteur(null);
        entity.setDestinataire(null);
        entity.setLivreur(null);
        entity.setZone(null);

        ColisDTO dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("colis-1");
        assertThat(dto.getClientExpediteurId()).isNull();
        assertThat(dto.getDestinataireId()).isNull();
        assertThat(dto.getLivreurId()).isNull();
        assertThat(dto.getZoneId()).isNull();
    }

    @Test
    @DisplayName("doit gérer les relations nulles (toEntity)")
    void testToEntity_NullHandling() {
        ColisDTO dto = new ColisDTO();
        dto.setClientExpediteurId("client-1");
        dto.setDestinataireId("dest-1");
        dto.setLivreurId(null);
        dto.setZoneId(null);

        Colis entity = mapper.toEntity(dto);

        assertThat(entity.getClientExpediteur()).isNotNull();
        assertThat(entity.getLivreur()).isNull();
        assertThat(entity.getZone()).isNull();
    }

    @Test
    @DisplayName("doit retourner null quand le DTO est null (toEntity)")
    void testToEntity_NullDto() {
        Colis entity = mapper.toEntity(null);

        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("doit retourner null quand l'entité est null (toDto)")
    void testToDto_NullEntity() {
        ColisDTO dto = mapper.toDto(null);

        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("doit gérer les cas où les IDs des relations sont null")
    void testToDto_NullRelationIds() {
        Colis entity = new Colis();

        ClientExpediteur client = new ClientExpediteur();
        client.setId(null);
        entity.setClientExpediteur(client);

        Destinataire dest = new Destinataire();
        dest.setId(null);
        entity.setDestinataire(dest);

        Livreur livreur = new Livreur();
        livreur.setId(null);
        entity.setLivreur(livreur);

        Zone zone = new Zone();
        zone.setId(null);
        entity.setZone(zone);

        ColisDTO dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getClientExpediteurId()).isNull();
        assertThat(dto.getDestinataireId()).isNull();
        assertThat(dto.getLivreurId()).isNull();
        assertThat(dto.getZoneId()).isNull();
    }

    @Test
    @DisplayName("doit mapper tous les champs d'un Colis vers ColisDTO")
    void testToDto_AllFields() {
        Colis entity = new Colis();
        entity.setId("colis-1");
        entity.setDescription("Description du colis");
        entity.setPoidsTotal(15.5);
        entity.setStatut(StatutColis.CREE);
        entity.setPriorite(Priorite.HAUTE);
        entity.setVilleDestination("Casablanca");
        entity.setDateCreation(LocalDateTime.of(2025, 11, 13, 10, 0));
        entity.setDateDernierStatut(LocalDateTime.of(2025, 11, 13, 11, 0));

        ClientExpediteur client = new ClientExpediteur();
        client.setId("client-1");
        entity.setClientExpediteur(client);

        Destinataire dest = new Destinataire();
        dest.setId("dest-1");
        entity.setDestinataire(dest);

        Livreur livreur = new Livreur();
        livreur.setId("livreur-1");
        entity.setLivreur(livreur);

        Zone zone = new Zone();
        zone.setId("zone-1");
        entity.setZone(zone);

        ColisDTO dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo("colis-1");
        assertThat(dto.getDescription()).isEqualTo("Description du colis");
        assertThat(dto.getPoidsTotal()).isEqualTo(15.5);
        assertThat(dto.getStatut()).isEqualTo(StatutColis.CREE);
        assertThat(dto.getPriorite()).isEqualTo(Priorite.HAUTE);
        assertThat(dto.getVilleDestination()).isEqualTo("Casablanca");
        assertThat(dto.getDateCreation()).isEqualTo(LocalDateTime.of(2025, 11, 13, 10, 0));
        assertThat(dto.getDateDernierStatut()).isEqualTo(LocalDateTime.of(2025, 11, 13, 11, 0));
        assertThat(dto.getClientExpediteurId()).isEqualTo("client-1");
        assertThat(dto.getDestinataireId()).isEqualTo("dest-1");
        assertThat(dto.getLivreurId()).isEqualTo("livreur-1");
        assertThat(dto.getZoneId()).isEqualTo("zone-1");
    }

    @Test
    @DisplayName("doit mapper tous les champs d'un ColisDTO vers Colis")
    void testToEntity_AllFields() {
        ColisDTO dto = new ColisDTO();
        dto.setId("colis-1");
        dto.setDescription("Description du colis");
        dto.setPoidsTotal(15.5);
        dto.setStatut(StatutColis.CREE);
        dto.setPriorite(Priorite.HAUTE);
        dto.setVilleDestination("Casablanca");
        dto.setDateCreation(LocalDateTime.of(2025, 11, 13, 10, 0));
        dto.setDateDernierStatut(LocalDateTime.of(2025, 11, 13, 11, 0));
        dto.setClientExpediteurId("client-1");
        dto.setDestinataireId("dest-1");
        dto.setLivreurId("livreur-1");
        dto.setZoneId("zone-1");

        Colis entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo("colis-1");
        assertThat(entity.getDescription()).isEqualTo("Description du colis");
        assertThat(entity.getPoidsTotal()).isEqualTo(15.5);
        assertThat(entity.getStatut()).isEqualTo(StatutColis.CREE);
        assertThat(entity.getPriorite()).isEqualTo(Priorite.HAUTE);
        assertThat(entity.getVilleDestination()).isEqualTo("Casablanca");
        assertThat(entity.getDateCreation()).isEqualTo(LocalDateTime.of(2025, 11, 13, 10, 0));
        assertThat(entity.getDateDernierStatut()).isEqualTo(LocalDateTime.of(2025, 11, 13, 11, 0));
        assertThat(entity.getClientExpediteur()).isNotNull();
        assertThat(entity.getClientExpediteur().getId()).isEqualTo("client-1");
        assertThat(entity.getDestinataire()).isNotNull();
        assertThat(entity.getDestinataire().getId()).isEqualTo("dest-1");
        assertThat(entity.getLivreur()).isNotNull();
        assertThat(entity.getLivreur().getId()).isEqualTo("livreur-1");
        assertThat(entity.getZone()).isNotNull();
        assertThat(entity.getZone().getId()).isEqualTo("zone-1");
    }

    @Test
    @DisplayName("doit mapper un Set vide de ColisProduit vers une liste vide de ColisProduitDTO")
    void testToDto_EmptyColisProduits() {
        Colis entity = new Colis();
        entity.setColisProduits(new HashSet<>());

        ClientExpediteur client = new ClientExpediteur();
        client.setId("client-1");
        entity.setClientExpediteur(client);

        ColisDTO dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getProduits()).isNotNull();
        assertThat(dto.getProduits()).isEmpty();
    }

    @Test
    @DisplayName("doit mapper un Set de ColisProduit vers une liste de ColisProduitDTO")
    void testToDto_WithColisProduits() {
        Colis entity = new Colis();
        entity.setId("colis-1");

        ClientExpediteur client = new ClientExpediteur();
        client.setId("client-1");
        entity.setClientExpediteur(client);

        ColisProduit cp1 = new ColisProduit();
        cp1.setId("cp-1");
        cp1.setQuantite(2);

        Produit produit1 = new Produit();
        produit1.setId("produit-1");
        cp1.setProduit(produit1);
        cp1.setColis(entity);

        ColisProduit cp2 = new ColisProduit();
        cp2.setId("cp-2");
        cp2.setQuantite(3);

        Produit produit2 = new Produit();
        produit2.setId("produit-2");
        cp2.setProduit(produit2);
        cp2.setColis(entity);

        Set<ColisProduit> colisProduits = new HashSet<>();
        colisProduits.add(cp1);
        colisProduits.add(cp2);
        entity.setColisProduits(colisProduits);

        ColisDTO dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getProduits()).isNotNull();
        assertThat(dto.getProduits()).hasSize(2);
        assertThat(dto.getProduits()).extracting(ColisProduitDTO::getProduitId).containsExactlyInAnyOrder("produit-1", "produit-2");
        assertThat(dto.getProduits()).extracting(ColisProduitDTO::getQuantite).containsExactlyInAnyOrder(2, 3);
    }

    @Test
    @DisplayName("doit gérer le cas où colisProduits est null")
    void testToDto_NullColisProduits() {
        Colis entity = new Colis();
        entity.setColisProduits(null);

        ClientExpediteur client = new ClientExpediteur();
        client.setId("client-1");
        entity.setClientExpediteur(client);

        ColisDTO dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getProduits()).isNull();
    }
}