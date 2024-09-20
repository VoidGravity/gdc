package com.baticuisine.dao;

import com.baticuisine.model.Component;
import com.baticuisine.model.Material;
import com.baticuisine.model.Labor;
import com.baticuisine.repository.ComponentRepository;
import com.baticuisine.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ComponentDAO implements ComponentRepository {
    private Connection connection;

    public ComponentDAO() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }

    @Override
    public void save(Component component) {
        String sql = "INSERT INTO components (nom, type_composant, taux_tva) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, component.getNom());
            pstmt.setString(2, component.getTypeComposant());
            pstmt.setDouble(3, component.getTauxTVA());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating component failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    component.setId(generatedKeys.getInt(1));

                    if (component instanceof Material) {
                        saveMaterialDetails((Material) component);
                    } else if (component instanceof Labor) {
                        saveLaborDetails((Labor) component);
                    }
                } else {
                    throw new SQLException("Creating component failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving component", e);
        }
    }

    private void saveMaterialDetails(Material material) throws SQLException {
        String sql = "INSERT INTO materials (component_id, cout_unitaire, quantite, cout_transport, coefficient_qualite) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, material.getId());
            pstmt.setDouble(2, material.getCoutUnitaire());
            pstmt.setDouble(3, material.getQuantite());
            pstmt.setDouble(4, material.getCoutTransport());
            pstmt.setDouble(5, material.getCoefficientQualite());
            pstmt.executeUpdate();
        }
    }

    private void saveLaborDetails(Labor labor) throws SQLException {
        String sql = "INSERT INTO labor (component_id, taux_horaire, heures_travail, productivite_ouvrier) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, labor.getId());
            pstmt.setDouble(2, labor.getTauxHoraire());
            pstmt.setDouble(3, labor.getHeuresTravail());
            pstmt.setDouble(4, labor.getProductiviteOuvrier());
            pstmt.executeUpdate();
        }
    }

    @Override
    public Optional<Component> findById(int id) {
        String sql = "SELECT c.*, m.cout_unitaire, m.quantite, m.cout_transport, m.coefficient_qualite, " +
                "l.taux_horaire, l.heures_travail, l.productivite_ouvrier " +
                "FROM components c " +
                "LEFT JOIN materials m ON c.id = m.component_id " +
                "LEFT JOIN labor l ON c.id = l.component_id " +
                "WHERE c.id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(extractComponentFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding component by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Component> findAll() {
        List<Component> components = new ArrayList<>();
        String sql = "SELECT c.*, m.cout_unitaire, m.quantite, m.cout_transport, m.coefficient_qualite, " +
                "l.taux_horaire, l.heures_travail, l.productivite_ouvrier " +
                "FROM components c " +
                "LEFT JOIN materials m ON c.id = m.component_id " +
                "LEFT JOIN labor l ON c.id = l.component_id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                components.add(extractComponentFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all components", e);
        }
        return components;
    }

    @Override
    public List<Component> findByProjectId(int projectId) {
        List<Component> components = new ArrayList<>();
        String sql = "SELECT c.*, m.cout_unitaire, m.quantite, m.cout_transport, m.coefficient_qualite, " +
                "l.taux_horaire, l.heures_travail, l.productivite_ouvrier " +
                "FROM components c " +
                "LEFT JOIN materials m ON c.id = m.component_id " +
                "LEFT JOIN labor l ON c.id = l.component_id " +
                "JOIN project_components pc ON c.id = pc.component_id " +
                "WHERE pc.project_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                components.add(extractComponentFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding components by project ID", e);
        }
        return components;
    }

    @Override
    public void update(Component component) {
        String sql = "UPDATE components SET nom = ?, type_composant = ?, taux_tva = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, component.getNom());
            pstmt.setString(2, component.getTypeComposant());
            pstmt.setDouble(3, component.getTauxTVA());
            pstmt.setInt(4, component.getId());
            pstmt.executeUpdate();

            if (component instanceof Material) {
                updateMaterialDetails((Material) component);
            } else if (component instanceof Labor) {
                updateLaborDetails((Labor) component);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating component", e);
        }
    }

    private void updateMaterialDetails(Material material) throws SQLException {
        String sql = "UPDATE materials SET cout_unitaire = ?, quantite = ?, cout_transport = ?, coefficient_qualite = ? WHERE component_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, material.getCoutUnitaire());
            pstmt.setDouble(2, material.getQuantite());
            pstmt.setDouble(3, material.getCoutTransport());
            pstmt.setDouble(4, material.getCoefficientQualite());
            pstmt.setInt(5, material.getId());
            pstmt.executeUpdate();
        }
    }

    private void updateLaborDetails(Labor labor) throws SQLException {
        String sql = "UPDATE labor SET taux_horaire = ?, heures_travail = ?, productivite_ouvrier = ? WHERE component_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, labor.getTauxHoraire());
            pstmt.setDouble(2, labor.getHeuresTravail());
            pstmt.setDouble(3, labor.getProductiviteOuvrier());
            pstmt.setInt(4, labor.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM components WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting component", e);
        }
    }

    private Component extractComponentFromResultSet(ResultSet rs) throws SQLException {
        String typeComposant = rs.getString("type_composant");
        Component component;
        if ("Matériel".equals(typeComposant)) {
            Material material = new Material();
            material.setCoutUnitaire(rs.getDouble("cout_unitaire"));
            material.setQuantite(rs.getDouble("quantite"));
            material.setCoutTransport(rs.getDouble("cout_transport"));
            material.setCoefficientQualite(rs.getDouble("coefficient_qualite"));
            component = material;
        } else {
            Labor labor = new Labor();
            labor.setTauxHoraire(rs.getDouble("taux_horaire"));
            labor.setHeuresTravail(rs.getDouble("heures_travail"));
            labor.setProductiviteOuvrier(rs.getDouble("productivite_ouvrier"));
            component = labor;
        }
        component.setId(rs.getInt("id"));
        component.setNom(rs.getString("nom"));
        component.setTypeComposant(typeComposant);
        component.setTauxTVA(rs.getDouble("taux_tva"));
        return component;
    }
}