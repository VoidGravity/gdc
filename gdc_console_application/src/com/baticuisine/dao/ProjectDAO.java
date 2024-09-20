package com.baticuisine.dao;

import com.baticuisine.model.Project;
import com.baticuisine.model.EtatProjet;
import com.baticuisine.repository.ProjectRepository;
import com.baticuisine.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjectDAO implements ProjectRepository {
    private Connection connection;

    public ProjectDAO() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }

    @Override
    public void save(Project project) {
        String sql = "INSERT INTO projects (nom_projet, marge_beneficiaire, cout_total, etat_projet, client_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, project.getNomProjet());
            pstmt.setDouble(2, project.getMargeBeneficiaire());
            pstmt.setDouble(3, project.getCoutTotal());
            pstmt.setString(4, project.getEtatProjet().name());
            pstmt.setInt(5, project.getClient().getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating project failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    project.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating project failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving project", e);
        }
    }

    @Override
    public Optional<Project> findById(int id) {
        String sql = "SELECT * FROM projects WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(extractProjectFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding project by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Project> findAll() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                projects.add(extractProjectFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all projects", e);
        }
        return projects;
    }

    @Override
    public List<Project> findByClientId(int clientId) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE client_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                projects.add(extractProjectFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding projects by client ID", e);
        }
        return projects;
    }

    @Override
    public void update(Project project) {
        String sql = "UPDATE projects SET nom_projet = ?, marge_beneficiaire = ?, cout_total = ?, etat_projet = ?, client_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, project.getNomProjet());
            pstmt.setDouble(2, project.getMargeBeneficiaire());
            pstmt.setDouble(3, project.getCoutTotal());
            pstmt.setString(4, project.getEtatProjet().name());
            pstmt.setInt(5, project.getClient().getId());
            pstmt.setInt(6, project.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating project", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM projects WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting project", e);
        }
    }

    @Override
    public void addComponentToProject(int projectId, int componentId) {
        String sql = "INSERT INTO project_components (project_id, component_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            pstmt.setInt(2, componentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding component to project", e);
        }
    }

    @Override
    public void removeComponentFromProject(int projectId, int componentId) {
        String sql = "DELETE FROM project_components WHERE project_id = ? AND component_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            pstmt.setInt(2, componentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing component from project", e);
        }
    }

    private Project extractProjectFromResultSet(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setId(rs.getInt("id"));
        project.setNomProjet(rs.getString("nom_projet"));
        project.setMargeBeneficiaire(rs.getDouble("marge_beneficiaire"));
        project.setCoutTotal(rs.getDouble("cout_total"));
        project.setEtatProjet(EtatProjet.valueOf(rs.getString("etat_projet")));
        // question to ask Mrabdelhafid , should i set client for fs
        return project;
    }
}