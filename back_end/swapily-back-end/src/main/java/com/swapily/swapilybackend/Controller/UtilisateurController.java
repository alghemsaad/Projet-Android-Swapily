package  com.swapily.swapilybackend.Controller;
import com.swapily.swapilybackend.Entity.*;
import com.swapily.swapilybackend.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/utilisateur")
@CrossOrigin(origins = "http://localhost:5173")
public class UtilisateurController {
    private final UtilisateurRepository utilisateurRepository;
    @Autowired
    private ProduitRepository produitRepository;
    @Autowired
    private DemmandeechangeRepository demmandechangeRepository;
    @Autowired
    private ImageEchangeRepository imageEchangeRepository;
    @Autowired
    private EvaliationRepository evaliationRepository;

    public UtilisateurController(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody Utilisateur utilisateur) {
        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        Utilisateur savedUser = utilisateurRepository.save(utilisateur);
        return ResponseEntity.ok(savedUser);
    }
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody Utilisateur utilisateur) {
        Utilisateur existingUser = utilisateurRepository.findByEmail(utilisateur.getEmail());
        if (existingUser == null || !existingUser.getMotdepasse().equals(utilisateur.getMotdepasse())) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
        return ResponseEntity.ok(existingUser);
    }
    @GetMapping("/all")
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable Long id) {
        return utilisateurRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    public ResponseEntity<Utilisateur> updateUtilisateur(@PathVariable Long id, @RequestBody Utilisateur updatedUser) {
        return utilisateurRepository.findById(id)
                .map(utilisateur -> {
                    utilisateur.setNom(updatedUser.getNom());
                    utilisateur.setPrenom(updatedUser.getPrenom());
                    utilisateur.setTelephone(updatedUser.getTelephone());
                    utilisateur.setAdress(updatedUser.getAdress());
                    utilisateur.setEmail(updatedUser.getEmail());
                    utilisateur.setMotdepasse(updatedUser.getMotdepasse());
                    utilisateur.setEtat_user(updatedUser.getEtat_user());
                    return ResponseEntity.ok(utilisateurRepository.save(utilisateur));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUtilisateurAvecCascade(@PathVariable Long id) {
        try {
            Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(id);
            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Utilisateur utilisateur = utilisateurOpt.get();

            // 1. Récupérer tous les produits de l'utilisateur
            List<Produit> produits = produitRepository.findByUtilisateurId(id);

            for (Produit produit : produits) {
                Long produitId = produit.getId();

                // Supprimer les évaluations liées au produit
                List<Evaliation> evaluationsProduit = evaliationRepository.findByProduitId(produitId);
                if (!evaluationsProduit.isEmpty()) {
                    evaliationRepository.deleteAll(evaluationsProduit);
                }

                // 2. Supprimer toutes les demandes d'échange liées à ce produit,
                // avec les images associées
                List<Demmandechange> demandesProduit = demmandechangeRepository.findByProduitId(produitId);
                for (Demmandechange demande : demandesProduit) {
                    Long demandeId = demande.getId();

                    // Supprimer images liées à la demande
                    List<ImageEchange> images = imageEchangeRepository.findByDemmandechangeId(demandeId);
                    if (!images.isEmpty()) {
                        imageEchangeRepository.deleteAll(images);
                    }

                    // Supprimer la demande
                    demmandechangeRepository.deleteById(demandeId);
                }

                // 3. Supprimer le produit lui-même
                produitRepository.delete(produit);
            }

            // Supprimer les évaluations où l'utilisateur est évalué
            List<Evaliation> evaluationsUtilisateurNotee = evaliationRepository.findByUtilisateurNoteeId(id);
            if (!evaluationsUtilisateurNotee.isEmpty()) {
                evaliationRepository.deleteAll(evaluationsUtilisateurNotee);
            }

            // Supprimer les évaluations où l'utilisateur est évaluateur
            List<Evaliation> evaluationsUtilisateurNoteur = evaliationRepository.findByUtilisateurNoteurId(id);
            if (!evaluationsUtilisateurNoteur.isEmpty()) {
                evaliationRepository.deleteAll(evaluationsUtilisateurNoteur);
            }

            // 4. Supprimer les demandes envoyées par cet utilisateur (pas encore supprimées)
            List<Demmandechange> demandesEnvoyees = demmandechangeRepository.findByUtilisateurId(id);
            for (Demmandechange demande : demandesEnvoyees) {
                Long demandeId = demande.getId();

                List<ImageEchange> images = imageEchangeRepository.findByDemmandechangeId(demandeId);
                if (!images.isEmpty()) {
                    imageEchangeRepository.deleteAll(images);
                }

                demmandechangeRepository.deleteById(demandeId);
            }

            // 5. Enfin supprimer l'utilisateur
            utilisateurRepository.delete(utilisateur);

            return ResponseEntity.ok().body("Utilisateur et ses données liées supprimés avec succès.");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur lors de la suppression en cascade : " + e.getMessage());
        }
    }


}