package com.baticuisine.service;

import com.baticuisine.dao.ProjetDAO;
import com.baticuisine.model.Projet;
import com.baticuisine.model.Client;
import com.baticuisine.model.Composant;
import com.baticuisine.model.Quote;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ProjetService {
    private final ProjetDAO projetDAO;

    public ProjetService(ProjetDAO projetDAO) {
        this.projetDAO = projetDAO;
    }

    public void creerProjet(String nomProjet, Client client) {
        Projet projet = new Projet(nomProjet, client);
        projetDAO.save(projet);
    }

    public void ajouterComposant(String nomProjet, Composant composant) {
        Optional<Projet> projetOpt = projetDAO.findByNom(nomProjet);
        projetOpt.ifPresent(projet -> {
            projet.ajouterComposant(composant);
            projetDAO.save(projet);
        });
    }

    public void calculerCoutTotal(String nomProjet) {
        Optional<Projet> projetOpt = projetDAO.findByNom(nomProjet);
        projetOpt.ifPresent(projet -> {
            projet.calculerCoutTotal();
            projetDAO.save(projet);
        });
    }

    public void genererDevis(String nomProjet, LocalDate dateValidite) {
        Optional<Projet> projetOpt = projetDAO.findByNom(nomProjet);
        projetOpt.ifPresent(projet -> {
            Quote devis = new Quote(projet.getCoutTotal(), LocalDate.now(), dateValidite);
            projet.setDevis(devis);
            projetDAO.save(projet);
        });
    }

    public List<Projet> obtenirTousProjets() {
        return projetDAO.findAll();
    }

    public Optional<Projet> obtenirProjetParNom(String nomProjet) {
        return projetDAO.findByNom(nomProjet);
    }

    public void mettreAJourMargeBeneficiaire(String nomProjet, double nouvelleMarge) {
        Optional<Projet> projetOpt = projetDAO.findByNom(nomProjet);
        projetOpt.ifPresent(projet -> {
            projet.setMargeBeneficiaire(nouvelleMarge);
            projet.calculerCoutTotal();
            projetDAO.save(projet);
        });
    }
}