package com.baticuisine.model;

public class MainOeuvre extends Composant {
    private double tauxHoraire;
    private double heuresTravail;
    private double productiviteOuvrier;

    public MainOeuvre(String nom, double tauxHoraire, double heuresTravail, double tauxTVA,
                      double productiviteOuvrier) {
        super(nom, tauxHoraire, heuresTravail, "Main-d'oeuvre", tauxTVA);
        this.tauxHoraire = tauxHoraire;
        this.heuresTravail = heuresTravail;
        this.productiviteOuvrier = productiviteOuvrier;
    }

    @Override
    public double calculerCoutTotal() {
        double coutHorsTVA = tauxHoraire * heuresTravail * productiviteOuvrier;
        return coutHorsTVA * (1 + tauxTVA);
    }

    // Getters and setters for tauxHoraire, heuresTravail, and productiviteOuvrier
}