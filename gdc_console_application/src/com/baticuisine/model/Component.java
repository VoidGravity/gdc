package com.baticuisine.model;

public abstract class Component {
    protected int id;
    protected String nom;
    protected String typeComposant;
    protected double tauxTVA;

    public Component() {}

    public Component(String nom, String typeComposant, double tauxTVA) {
        this.nom = nom;
        this.typeComposant = typeComposant;
        this.tauxTVA = tauxTVA;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTypeComposant() {
        return typeComposant;
    }

    public void setTypeComposant(String typeComposant) {
        this.typeComposant = typeComposant;
    }

    public double getTauxTVA() {
        return tauxTVA;
    }

    public void setTauxTVA(double tauxTVA) {
        this.tauxTVA = tauxTVA;
    }

    public abstract double calculateCost();

    @Override
    public String toString() {
        return "Component{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", typeComposant='" + typeComposant + '\'' +
                ", tauxTVA=" + tauxTVA +
                '}';
    }
}