package com.smartlogi.sdms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("LIVREUR")
@Getter
@Setter
@NoArgsConstructor
public class Livreur extends Utilisateur {

    @Column(name = "vehicule")
    private String vehicule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private Zone zone; // Assure-toi que l'entité Zone.java existe

    // Getters/Setters pour 'vehicule' et 'zone' gérés par Lombok
}
