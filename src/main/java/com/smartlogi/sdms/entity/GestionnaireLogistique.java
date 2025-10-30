package com.smartlogi.sdms.entity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("GESTIONNAIRE")
@Getter
@Setter
@NoArgsConstructor
public class GestionnaireLogistique extends Utilisateur {

}
