package com.swapily.swapilybackend.Controller;

import com.swapily.swapilybackend.Entity.*;
import com.swapily.swapilybackend.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/evaluation")
@CrossOrigin(origins = "http://localhost:5173")
public class EvaliationController {

    @Autowired
    private EvaliationRepository evaluationRepository;
    
    @GetMapping("/utilisateur/{id}")
    public List<Evaliation> getEvaluationsByUtilisateurNotee(@PathVariable Long id) {
        return evaluationRepository.findByUtilisateurNoteeId(id);
    }

    @PostMapping("/ajouter")
    @ResponseStatus(HttpStatus.CREATED)
    public Evaliation ajouterEvaluation(@RequestBody Evaliation evaluation) {
        if (evaluationRepository.findByUtilisateurNoteurIdAndProduitId(
                evaluation.getUtilisateurNoteurId(),
                evaluation.getProduitId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Vous avez déjà noté ce produit");
        }
        evaluation.setDateEvaluation(LocalDate.now().toString());
        return evaluationRepository.save(evaluation);
    }
}