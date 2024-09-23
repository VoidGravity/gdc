package com.baticuisine.service;

import com.baticuisine.model.Project;
import com.baticuisine.model.Component;
import com.baticuisine.model.EtatProjet;
import com.baticuisine.repository.ProjectRepository;
import com.baticuisine.repository.ComponentRepository;
import java.util.ArrayList;
import com.baticuisine.model.Material;
import com.baticuisine.model.Labor;
import java.util.List;
import java.util.Optional;

public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ComponentRepository componentRepository;

    public ProjectService(ProjectRepository projectRepository, ComponentRepository componentRepository) {
        this.projectRepository = projectRepository;
        this.componentRepository = componentRepository;
    }

    public void addComponentToProject(int projectId, int componentId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        Optional<Component> componentOpt = componentRepository.findById(componentId);

        if (projectOpt.isPresent() && componentOpt.isPresent()) {
            Project project = projectOpt.get();
            Component component = componentOpt.get();

            if (project.getComponents().stream().noneMatch(c -> c.getId() == componentId)) {
                projectRepository.addComponentToProject(projectId, componentId);
                project.addComponent(component);  // Add this line
                updateProjectCost(project);
                System.out.println("Component added to project successfully.");
            } else {
                System.out.println("The component is already in the project.");
            }
        } else {
            if (projectOpt.isEmpty()) {
                throw new IllegalArgumentException("Project not found with ID: " + projectId);
            }
            if (componentOpt.isEmpty()) {
                throw new IllegalArgumentException("Component not found with ID: " + componentId);
            }
        }
    }

    public void createProject(Project project) {
        validateProject(project);
        projectRepository.save(project);
    }
//    public void createProject(Project project, int clientId) {
//        Client client = clientRepository.findById(clientId)
//                .orElseThrow(() -> new IllegalArgumentException("Client not found with ID: " + clientId));
//        project.setClient(client);
//        projectRepository.save(project);
//    }

    public Optional<Project> getProjectById(int id) {
        return projectRepository.findById(id);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getProjectsByClientId(int clientId) {
        return projectRepository.findByClientId(clientId);
    }
    public String getProjectSummary(int projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));

        double totalMaterialCost = 0;
        double totalLaborCost = 0;

        for (Component component : project.getComponents()) {
            if (component instanceof Material) {
                totalMaterialCost += component.calculateCost();
            } else if (component instanceof Labor) {
                totalLaborCost += component.calculateCost();
            }
        }

        double totalCost = totalMaterialCost + totalLaborCost;
        double finalCost = totalCost * (1 + project.getMargeBeneficiaire());

        StringBuilder summary = new StringBuilder();
        summary.append("Project Summary for '").append(project.getNomProjet()).append("'\n");
        summary.append("Total Material Cost: ").append(String.format("%.2f", totalMaterialCost)).append("\n");
        summary.append("Total Labor Cost: ").append(String.format("%.2f", totalLaborCost)).append("\n");
        summary.append("Total Cost: ").append(String.format("%.2f", totalCost)).append("\n");
        summary.append("Profit Margin: ").append(String.format("%.2f%%", project.getMargeBeneficiaire() * 100)).append("\n");
        summary.append("Final Project Cost: ").append(String.format("%.2f", finalCost));

        return summary.toString();
    }

    private void updateProjectCost(Project project) {
        if (project.getComponents() == null) {
            project.setComponents(new ArrayList<>());
        }
        double totalCost = project.getComponents().stream()
                .mapToDouble(Component::calculateCost)
                .sum();
        project.setCoutTotal(totalCost * (1 + project.getMargeBeneficiaire()));
        projectRepository.update(project);
    }

    public void deleteProject(int id) {
        projectRepository.delete(id);
    }





    public void removeComponentFromProject(int projectId, int componentId) {
        projectRepository.removeComponentFromProject(projectId, componentId);
        updateProjectCost(projectId);
    }

    private void validateProject(Project project) {
        if (project.getNomProjet() == null || project.getNomProjet().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du projet ne peut pas être vide");
        }
        if (project.getMargeBeneficiaire() < 0) {
            throw new IllegalArgumentException("La marge bénéficiaire ne peut pas être négative");
        }
        if (project.getClient() == null) {
            throw new IllegalArgumentException("Le projet doit être associé à un client");
        }
        // Add more validation as needed
    }
    public void updateProject(Project project) {
        validateProject(project);
        projectRepository.update(project);
    }
    private void updateProjectCost(int projectId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            List<Component> components = componentRepository.findByProjectId(projectId);
            double totalCost = components.stream()
                    .mapToDouble(Component::calculateCost)
                    .sum();
            project.setCoutTotal(totalCost * (1 + project.getMargeBeneficiaire()));
            projectRepository.update(project);
        }
    }

    public void updateProjectStatus(int projectId, EtatProjet newStatus) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            project.setEtatProjet(newStatus);
            projectRepository.update(project);
        } else {
            throw new IllegalArgumentException("Projet non trouvé avec l'ID : " + projectId);
        }
    }

    public double calculateTotalCost(int projectId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            List<Component> components = componentRepository.findByProjectId(projectId);
            double componentCost = components.stream()
                    .mapToDouble(Component::calculateCost)
                    .sum();
            return componentCost * (1 + project.getMargeBeneficiaire());
        } else {
            throw new IllegalArgumentException("Projet non trouvé avec l'ID : " + projectId);
        }
    }
}