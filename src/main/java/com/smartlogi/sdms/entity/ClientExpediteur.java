package com.smartlogi.sdms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("CLIENT_EXPEDITEUR")
@Getter
@Setter
@NoArgsConstructor
public class ClientExpediteur extends Utilisateur {
    @Column(name = "adresse")
    private String adresse;
}
