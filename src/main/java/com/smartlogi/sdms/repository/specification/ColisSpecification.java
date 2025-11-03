package com.smartlogi.sdms.repository.specification;

import com.smartlogi.sdms.entity.Colis;
import com.smartlogi.sdms.entity.Zone;
import com.smartlogi.sdms.entity.enumeration.Priorite;
import com.smartlogi.sdms.entity.enumeration.StatutColis;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;


public class ColisSpecification {

    public static Specification<Colis> findByCriteria(StatutColis statut, String zoneId, String ville, Priorite priorite) {

        return (root, query, criteriaBuilder) -> {



            List<Predicate> predicates = new ArrayList<>();

            if (statut != null) {
                predicates.add(criteriaBuilder.equal(root.get("statut"), statut));
            }

            if (StringUtils.hasText(zoneId)) {
                Join<Colis, Zone> zoneJoin = root.join("zone");
                predicates.add(criteriaBuilder.equal(zoneJoin.get("id"), zoneId));
            }

            if (StringUtils.hasText(ville)) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("villeDestination")),
                        "%" + ville.toLowerCase() + "%"
                ));
            }

            if (priorite != null) {
                predicates.add(criteriaBuilder.equal(root.get("priorite"), priorite));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}