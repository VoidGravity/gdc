package com.baticuisine.service;

import com.baticuisine.model.Component;
import com.baticuisine.model.Material;
import com.baticuisine.model.Labor;
import com.baticuisine.repository.ComponentRepository;

import java.util.List;
import java.util.Optional;

public class ComponentService {
    private final ComponentRepository componentRepository;

    public ComponentService(ComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }

    public void createComponent(Component component) {
        validateComponent(component);
        componentRepository.save(component);
    }

    public Optional<Component> getComponentById(int id) {
        return componentRepository.findById(id);
    }

    public List<Component> getAllComponents() {
        return componentRepository.findAll();
    }

    public List<Component> getComponentsByProjectId(int projectId) {
        return componentRepository.findByProjectId(projectId);
    }

    public void updateComponent(Component component) {
        validateComponent(component);
        componentRepository.update(component);
    }

    public void deleteComponent(int id) {
        componentRepository.delete(id);
    }

    private void validateComponent(Component component) {
        if (component.getNom() == null || component.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du composant ne peut pas être vide");
        }
        if (component.getTauxTVA() < 0) {
            throw new IllegalArgumentException("Le taux de TVA ne peut pas être négatif");
        }
        if (component instanceof Material) {
            validateMaterial((Material) component);
        } else if (component instanceof Labor) {
            validateLabor((Labor) component);
        }
    }

    private void validateMaterial(Material material) {
        if (material.getCoutUnitaire() < 0) {
            throw new IllegalArgumentException("Le coût unitaire ne peut pas être négatif");
        }
        if (material.getQuantite() < 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être négative");
        }
        if (material.getCoefficientQualite() <= 0) {
            throw new IllegalArgumentException("Le coefficient de qualité doit être supérieur à zéro");
        }
    }

    private void validateLabor(Labor labor) {
        if (labor.getTauxHoraire() < 0) {
            throw new IllegalArgumentException("Le taux horaire ne peut pas être négatif");
        }
        if (labor.getHeuresTravail() < 0) {
            throw new IllegalArgumentException("Les heures de travail ne peuvent pas être négatives");
        }
        if (labor.getProductiviteOuvrier() <= 0) {
            throw new IllegalArgumentException("La productivité de l'ouvrier doit être supérieure à zéro");
        }
    }

    public double calculateComponentCost(Component component) {
        return component.calculateCost();
    }

    public double calculateTotalComponentCost(List<Component> components) {
        return components.stream()
                .mapToDouble(this::calculateComponentCost)
                .sum();
    }

    public double calculateTotalMaterialCost(List<Component> components) {
        return components.stream()
                .filter(c -> c instanceof Material)
                .mapToDouble(this::calculateComponentCost)
                .sum();
    }

    public double calculateTotalLaborCost(List<Component> components) {
        return components.stream()
                .filter(c -> c instanceof Labor)
                .mapToDouble(this::calculateComponentCost)
                .sum();
    }
}