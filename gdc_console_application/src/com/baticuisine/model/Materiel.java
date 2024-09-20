package com.baticuisine.model;

public class Materiel extends Composant {
    private double coutTransport;
    private double coefficientQualite;

    public Materiel(String nom, double coutUnitaire, double quantite, double tauxTVA,
                    double coutTransport, double coefficientQualite) {
        super(nom, coutUnitaire, quantite, "Materiel", tauxTVA);
        this.coutTransport = coutTransport;
        this.coefficientQualite = coefficientQualite;
    }

    @Override
    public double calculerCoutTotal() {
        double coutHorsTVA = (coutUnitaire * quantite + coutTransport) * coefficientQualite;
        return coutHorsTVA * (1 + tauxTVA);
    }

    // Getters and setters for coutTransport and coefficientQualite
}