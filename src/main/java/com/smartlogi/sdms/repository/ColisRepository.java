package com.smartlogi.sdms.repository;
import com.smartlogi.sdms.entity.Colis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ColisRepository extends JpaRepository<Colis, String>, JpaSpecificationExecutor<Colis> {
    Page<Colis> findAllByClientExpediteurId(String clientExpediteurId, Pageable pageable);
}
