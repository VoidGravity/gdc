package com.baticuisine.dao;

import com.baticuisine.model.*;

import com.baticuisine.repository.ProjectRepository;
import com.baticuisine.db.DatabaseConnection;

import java.sql.*;
import java.util.*;

public class ProjectDAO implements ProjectRepository {
    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }
    @Override
    public void save(Project project) {
        String sql = "INSERT INTO projects (nom_projet, marge_beneficiaire, cout_total, etat_projet, client_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
    public List<Project> findAll() {
        Map<Integer, Project> projectMap = new HashMap<>();
        String sql = "SELECT p.id AS project_id, p.nom_projet, p.marge_beneficiaire, p.cout_total, p.etat_projet, " +
                "cl.id AS client_id, cl.nom AS client_nom, cl.adresse AS client_adresse, " +
                "cl.telephone AS client_telephone, cl.est_professionnel AS client_est_professionnel, " +
                "c.id AS component_id, c.nom AS component_nom, c.type_composant, c.taux_tva " +
                "FROM projects p " +
                "LEFT JOIN clients cl ON p.client_id = cl.id " +
                "LEFT JOIN project_components pc ON p.id = pc.project_id " +
                "LEFT JOIN components c ON pc.component_id = c.id " +
                "ORDER BY p.id, c.id";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int projectId = rs.getInt("project_id");
                Project project = projectMap.get(projectId);

                if (project == null) {
                    project = extractProjectFromResultSet(rs);
                    project.setComponents(new ArrayList<>());
                    project.setClient(extractClientFromResultSet(rs));
                    projectMap.put(projectId, project);
                }

                Component component = extractComponentFromResultSet(rs);
                if (component != null) {
                    project.addComponent(component);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all projects", e);
        }
        return new ArrayList<>(projectMap.values());
    }
    @Override
    public List<Project> findByClientId(int clientId) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE client_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
    public Optional<Project> findById(int id) {
        String sql = "SELECT p.id AS project_id, p.nom_projet, p.marge_beneficiaire, p.cout_total, p.etat_projet, " +
                "cl.id AS client_id, cl.nom AS client_nom, cl.adresse AS client_adresse, " +
                "cl.telephone AS client_telephone, cl.est_professionnel AS client_est_professionnel, " +
                "c.id AS component_id, c.nom AS component_nom, c.type_composant, c.taux_tva " +
                "FROM projects p " +
                "LEFT JOIN clients cl ON p.client_id = cl.id " +
                "LEFT JOIN project_components pc ON p.id = pc.project_id " +
                "LEFT JOIN components c ON pc.component_id = c.id " +
                "WHERE p.id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                Project project = null;

                while (rs.next()) {
                    if (project == null) {
                        project = extractProjectFromResultSet(rs);
                        project.setComponents(new ArrayList<>());
                        project.setClient(extractClientFromResultSet(rs));
                    }

                    Component component = extractComponentFromResultSet(rs);
                    if (component != null) {
                        project.addComponent(component);
                    }
                }

                return Optional.ofNullable(project);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding project by ID", e);
        }
    }

    private Client extractClientFromResultSet(ResultSet rs) throws SQLException {
        int clientId = rs.getInt("client_id");
        if (rs.wasNull()) return null;

        Client client = new Client();
        client.setId(clientId);
        client.setNom(rs.getString("client_nom"));
        client.setAdresse(rs.getString("client_adresse"));
        client.setTelephone(rs.getString("client_telephone"));
        client.setEstProfessionnel(rs.getBoolean("client_est_professionnel"));
        return client;
    }

    @Override
    public void update(Project project) {
        String sql = "UPDATE projects SET nom_projet = ?, marge_beneficiaire = ?, cout_total = ?, etat_projet = ?, client_id = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, project.getNomProjet());
            pstmt.setDouble(2, project.getMargeBeneficiaire());
            pstmt.setDouble(3, project.getCoutTotal());
            pstmt.setString(4, project.getEtatProjet().name());
            if (project.getClient() != null) {
                pstmt.setInt(5, project.getClient().getId());
            } else {
                pstmt.setNull(5, java.sql.Types.INTEGER);
            }
            pstmt.setInt(6, project.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating project", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM projects WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting project", e);
        }
    }
    private Project extractProjectFromResultSet(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setId(rs.getInt("project_id"));
        project.setNomProjet(rs.getString("nom_projet"));
        project.setMargeBeneficiaire(rs.getDouble("marge_beneficiaire"));
        project.setCoutTotal(rs.getDouble("cout_total"));
        project.setEtatProjet(EtatProjet.valueOf(rs.getString("etat_projet")));
        return project;
    }
    private Component extractComponentFromResultSet(ResultSet rs) throws SQLException {
        int componentId = rs.getInt("component_id");
        if (rs.wasNull()) return null;

        String typeComposant = rs.getString("type_composant");
        Component component;

        if ("Matériel".equals(typeComposant)) {
            component = new Material();
        } else if ("Main-d'œuvre".equals(typeComposant)) {
            component = new Labor();
        } else {
            throw new IllegalStateException("Unknown component type: " + typeComposant);
        }

        component.setId(componentId);
        component.setNom(rs.getString("component_nom"));
        component.setTypeComposant(typeComposant);
        component.setTauxTVA(rs.getDouble("taux_tva"));

        return component;
    }
    @Override
    public void addComponentToProject(int projectId, int componentId) {
        String sql = "INSERT INTO project_components (project_id, component_id) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            pstmt.setInt(2, componentId);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Adding component to project failed, no rows affected.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding component to project", e);
        }
    }

    @Override
    public void removeComponentFromProject(int projectId, int componentId) {
        String sql = "DELETE FROM project_components WHERE project_id = ? AND component_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            pstmt.setInt(2, componentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing component from project", e);
        }
    }


}