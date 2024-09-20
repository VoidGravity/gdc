package com.baticuisine.service;

import com.baticuisine.model.Project;
import com.baticuisine.model.Component;
import com.baticuisine.model.EtatProjet;
import com.baticuisine.repository.ProjectRepository;
import com.baticuisine.repository.ComponentRepository;

import java.util.List;
import java.util.Optional;

public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ComponentRepository componentRepository;

    public ProjectService(ProjectRepository projectRepository, ComponentRepository componentRepository) {
        this.projectRepository = projectRepository;
        this.componentRepository = componentRepository;
    }

    public void createProject(Project project) {
        validateProject(project);
        projectRepository.save(project);
    }

    public Optional<Project> getProjectById(int id) {
        return projectRepository.findById(id);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getProjectsByClientId(int clientId) {
        return projectRepository.findByClientId(clientId);
    }

    public void updateProject(Project project) {
        validateProject(project);
        projectRepository.update(project);
    }

    public void deleteProject(int id) {
        projectRepository.delete(id);
    }

    public void addComponentToProject(int projectId, int componentId) {
        Optional<Project> projectOpt = projectRepository.findById(projectId);
        Optional<Component> componentOpt = componentRepository.findById(componentId);

        if (projectOpt.isPresent() && componentOpt.isPresent()) {
            Project project = projectOpt.get();
            Component component = componentOpt.get();

            projectRepository.addComponentToProject(projectId, componentId);
            updateProjectCost(project);
        } else {
            throw new IllegalArgumentException("Project or Component not found");
        }
    }

    private void updateProjectCost(Project project) {
        List<Component> components = componentRepository.findByProjectId(project.getId());
        double totalCost = components.stream()
                .mapToDouble(Component::calculateCost)
                .sum();
        project.setCoutTotal(totalCost * (1 + project.getMargeBeneficiaire()));
        projectRepository.update(project);
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