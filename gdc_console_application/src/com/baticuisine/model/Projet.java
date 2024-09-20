package com.baticuisine.model;

import java.util.ArrayList;
import java.util.List;

public class Projet {
    private String nomProjet;
    private double margeBeneficiaire;
    private double coutTotal;
    private EtatProjet etatProjet;
    private Client client;
    private List<Composant> composants;
    private Devis devis;

    public Projet(String nomProjet, Client client) {
        this.nomProjet = nomProjet;
        this.client = client;
        this.etatProjet = EtatProjet.EN_COURS;
        this.composants = new ArrayList<>();
        this.margeBeneficiaire = 0.0;
        this.coutTotal = 0.0;
    }

    public String getNomProjet() {
        return nomProjet;
    }

    public void setNomProjet(String nomProjet) {
        this.nomProjet = nomProjet;
    }

    public double getMargeBeneficiaire() {
        return margeBeneficiaire;
    }

    public void setMargeBeneficiaire(double margeBeneficiaire) {
        this.margeBeneficiaire = margeBeneficiaire;
    }

    public double getCoutTotal() {
        return coutTotal;
    }

    public void setCoutTotal(double coutTotal) {
        this.coutTotal = coutTotal;
    }

    public EtatProjet getEtatProjet() {
        return etatProjet;
    }

    public void setEtatProjet(EtatProjet etatProjet) {
        this.etatProjet = etatProjet;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Composant> getComposants() {
        return composants;
    }

    public void setComposants(List<Composant> composants) {
        this.composants = composants;
    }

    public Devis getDevis() {
        return devis;
    }

    public void setDevis(Devis devis) {
        this.devis = devis;
    }

    public void ajouterComposant(Composant composant) {
        this.composants.add(composant);
    }

    public void calculerCoutTotal() {
        double coutMateriauxEtMainOeuvre = composants.stream()
                .mapToDouble(Composant::calculerCoutTotal)
                .sum();
        this.coutTotal = coutMateriauxEtMainOeuvre * (1 + margeBeneficiaire);
    }

    @Override
    public String toString() {
        return "Projet{" +
                "nomProjet='" + nomProjet + '\'' +
                ", margeBeneficiaire=" + margeBeneficiaire +
                ", coutTotal=" + coutTotal +
                ", etatProjet=" + etatProjet +
                ", client=" + client +
                ", composants=" + composants +
                ", devis=" + devis +
                '}';
    }
}