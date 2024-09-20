package com.baticuisine.model;

public class Material extends Component {
    private double coutUnitaire;
    private double quantite;
    private double coutTransport;
    private double coefficientQualite;

    public Material() {
        super();
    }

    public Material(String nom, double tauxTVA, double coutUnitaire, double quantite, double coutTransport, double coefficientQualite) {
        super(nom, "Matériel", tauxTVA);
        this.coutUnitaire = coutUnitaire;
        this.quantite = quantite;
        this.coutTransport = coutTransport;
        this.coefficientQualite = coefficientQualite;
    }

    public double getCoutUnitaire() {
        return coutUnitaire;
    }

    public void setCoutUnitaire(double coutUnitaire) {
        this.coutUnitaire = coutUnitaire;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public double getCoutTransport() {
        return coutTransport;
    }

    public void setCoutTransport(double coutTransport) {
        this.coutTransport = coutTransport;
    }

    public double getCoefficientQualite() {
        return coefficientQualite;
    }

    public void setCoefficientQualite(double coefficientQualite) {
        this.coefficientQualite = coefficientQualite;
    }

    @Override
    public double calculateCost() {
        return (coutUnitaire * quantite * coefficientQualite + coutTransport) * (1 + tauxTVA / 100);
    }

    @Override
    public String toString() {
        return "Material{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", typeComposant='" + typeComposant + '\'' +
                ", tauxTVA=" + tauxTVA +
                ", coutUnitaire=" + coutUnitaire +
                ", quantite=" + quantite +
                ", coutTransport=" + coutTransport +
                ", coefficientQualite=" + coefficientQualite +
                '}';
    }
}