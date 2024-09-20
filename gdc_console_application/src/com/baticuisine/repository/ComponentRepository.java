package com.baticuisine.repository;

import com.baticuisine.model.Component;
import java.util.List;
import java.util.Optional;

public interface ComponentRepository {
    void save(Component component);
    Optional<Component> findById(int id);
    List<Component> findAll();
    List<Component> findByProjectId(int projectId);
    void update(Component component);
    void delete(int id);
}