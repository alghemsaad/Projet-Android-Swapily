package com.swapily.swapilybackend.Repository;

import com.swapily.swapilybackend.Entity.ImageEchange;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ImageEchangeRepository extends JpaRepository<ImageEchange, Long> {
    List<ImageEchange> findByProduitLieId(Long produitLieId);
    List<ImageEchange> findByUtilisateurId(Long utilisateurId);
    List<ImageEchange> findByDemmandechangeId(Long demmandechangeId);

}
