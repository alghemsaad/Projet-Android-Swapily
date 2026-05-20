package com.swapily.swapilybackend.Repository;


import com.swapily.swapilybackend.Entity.Demmandechange;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DemmandeechangeRepository extends JpaRepository<Demmandechange, Long> {
    List<Demmandechange> findByUtilisateurIdOrderByIdDesc(Long utilisateurId);
    List<Demmandechange> findByUtilisateurIdAndStatutOrderByIdDesc(Long utilisateurId, String statut);
    List<Demmandechange> findByProduitUtilisateurIdAndStatutOrderByIdDesc(Long utilisateurId, String statut);


    List<Demmandechange> findByProduitId(Long produitId);

    List<Demmandechange> findByUtilisateurId(Long id);

    List<Demmandechange> findAllByOrderByIdDesc();

    List<Demmandechange> findByUtilisateurIdAndProduitId(Long utilisateurId, Long produitId);

    Optional<Demmandechange> findFirstByProduitIdAndStatut(Long produitId, String valide);
}




