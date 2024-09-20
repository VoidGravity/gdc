package com.baticuisine.repository;

import com.baticuisine.model.Project;
import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    void save(Project project);
    Optional<Project> findById(int id);
    List<Project> findAll();
    List<Project> findByClientId(int clientId);
    void update(Project project);
    void delete(int id);
    void addComponentToProject(int projectId, int componentId);
    void removeComponentFromProject(int projectId, int componentId);
}