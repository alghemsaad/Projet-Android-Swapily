package com.swapily.swapilybackend.Repository;
import com.swapily.swapilybackend.Entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {
    List<Produit> findByStatutOrderByIdDesc(String statut);
    List<Produit> findAllByOrderByIdDesc();
    List<Produit> findByUtilisateurId(Long id);
}
