package com.swapily.swapilybackend.Controller;

import com.swapily.swapilybackend.Entity.*;
import com.swapily.swapilybackend.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/demmandechange")
@CrossOrigin(origins = "http://localhost:5173")
public class DemmandeechangeController {

    @Autowired
    private DemmandeechangeRepository demmandechangeRepository;
    @Autowired
    private ImageEchangeRepository imageEchangeRepository;

    @PostMapping("/ajouter")
    public ResponseEntity<?> ajouterDemande(@RequestBody Demmandechange demande) {
        Demmandechange saved = demmandechangeRepository.save(demande);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/envoyees/{utilisateurId}")
    public ResponseEntity<List<Demmandechange>> getDemandesEnvoyees(@PathVariable Long utilisateurId) {
        List<Demmandechange> demandes = demmandechangeRepository.findByUtilisateurIdOrderByIdDesc(utilisateurId);
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/envoyees/en_attente/{utilisateurId}")
    public ResponseEntity<List<Demmandechange>> getDemandesEnAttente(@PathVariable Long utilisateurId) {
        List<Demmandechange> demandes = demmandechangeRepository.findByUtilisateurIdAndStatutOrderByIdDesc(utilisateurId, "en attente");
        return ResponseEntity.ok(demandes);
    }

    // هاد الميثود الجديدة: تجيب طلبات وصلات للمنتوجات ديال مول المنتج
    @GetMapping("/recues/en_attente/{utilisateurId}")
    public ResponseEntity<List<Demmandechange>> getDemandesRecuesEnAttente(@PathVariable Long utilisateurId) {
        List<Demmandechange> demandes = demmandechangeRepository.findByProduitUtilisateurIdAndStatutOrderByIdDesc(utilisateurId, "en attente");
        return ResponseEntity.ok(demandes);
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<?> updateStatut(@PathVariable Long id, @RequestParam String statut) {
        Optional<Demmandechange> optDemande = demmandechangeRepository.findById(id);
        if (optDemande.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Demmandechange demande = optDemande.get();
        demande.setStatut(statut);
        demmandechangeRepository.save(demande);
        return ResponseEntity.ok(demande);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDemandeAvecImage(@PathVariable Long id) {
        // Chercher la demande
        Optional<Demmandechange> demandeOpt = demmandechangeRepository.findById(id);
        if (demandeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Supprimer d'abord les images liées à cette demande
        List<ImageEchange> images = imageEchangeRepository.findByDemmandechangeId(id);
        if (!images.isEmpty()) {
            imageEchangeRepository.deleteAll(images);
        }

        // Ensuite supprimer la demande
        demmandechangeRepository.deleteById(id);

        return ResponseEntity.ok().body("Demande et image associée supprimées.");
    }
    @GetMapping("/all")
    public ResponseEntity<List<Demmandechange>> getAllDemandes() {
        List<Demmandechange> demandes = demmandechangeRepository.findAllByOrderByIdDesc();
        return ResponseEntity.ok(demandes);
    }

    @GetMapping("/utilisateur/{utilisateurId}/produit/{produitId}")
    public ResponseEntity<List<Demmandechange>> getDemandesParUtilisateurEtProduit(
            @PathVariable Long utilisateurId,
            @PathVariable Long produitId) {
        List<Demmandechange> demandes = demmandechangeRepository
                .findByUtilisateurIdAndProduitId(utilisateurId, produitId);
        return ResponseEntity.ok(demandes);
    }
    @GetMapping("/valide/produit/{produitId}")
    public ResponseEntity<Demmandechange> getDemandeValideParProduit(@PathVariable Long produitId) {
        Optional<Demmandechange> demande = demmandechangeRepository
                .findFirstByProduitIdAndStatut(produitId, "valide");
        return demande.map(ResponseEntity::ok).orElse(ResponseEntity.noContent().build());
    }
}
