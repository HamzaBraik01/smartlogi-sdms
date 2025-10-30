package com.smartlogi.sdms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("DESTINATAIRE")
@Getter
@Setter
@NoArgsConstructor
public class Destinataire extends Utilisateur{
    @Column(name = "adresse")
    private String adresse;
}
