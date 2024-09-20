package com.baticuisine.model;

public class Labor extends Component {
    private double tauxHoraire;
    private double heuresTravail;
    private double productiviteOuvrier;

    public Labor() {
        super();
    }

    public Labor(String nom, double tauxTVA, double tauxHoraire, double heuresTravail, double productiviteOuvrier) {
        super(nom, "Main-d'œuvre", tauxTVA);
        this.tauxHoraire = tauxHoraire;
        this.heuresTravail = heuresTravail;
        this.productiviteOuvrier = productiviteOuvrier;
    }

    public double getTauxHoraire() {
        return tauxHoraire;
    }

    public void setTauxHoraire(double tauxHoraire) {
        this.tauxHoraire = tauxHoraire;
    }

    public double getHeuresTravail() {
        return heuresTravail;
    }

    public void setHeuresTravail(double heuresTravail) {
        this.heuresTravail = heuresTravail;
    }

    public double getProductiviteOuvrier() {
        return productiviteOuvrier;
    }

    public void setProductiviteOuvrier(double productiviteOuvrier) {
        this.productiviteOuvrier = productiviteOuvrier;
    }

    @Override
    public double calculateCost() {
        return tauxHoraire * heuresTravail * productiviteOuvrier * (1 + tauxTVA / 100);
    }

    @Override
    public String toString() {
        return "Labor{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", typeComposant='" + typeComposant + '\'' +
                ", tauxTVA=" + tauxTVA +
                ", tauxHoraire=" + tauxHoraire +
                ", heuresTravail=" + heuresTravail +
                ", productiviteOuvrier=" + productiviteOuvrier +
                '}';
    }
}