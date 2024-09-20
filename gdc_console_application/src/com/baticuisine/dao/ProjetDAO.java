package com.baticuisine.dao;

import com.baticuisine.model.Projet;
import com.baticuisine.model.EtatProjet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProjetDAO {
    private static final String URL = "jdbc:postgresql://localhost:5432/baticuisine";
    private static final String USER = "myuser";
    private static final String PASSWORD = "AZERAZER1234";

    public void save(Projet projet) {
        String sql = "INSERT INTO projets (nom_projet, marge_beneficiaire, cout_total, etat_projet, client_id) VALUES (?, ?, ?, ?, ?) ON CONFLICT (nom_projet) DO UPDATE SET marge_beneficiaire = EXCLUDED.marge_beneficiaire, cout_total = EXCLUDED.cout_total, etat_projet = EXCLUDED.etat_projet, client_id = EXCLUDED.client_id";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, projet.getNomProjet());
            pstmt.setDouble(2, projet.getMargeBeneficiaire());
            pstmt.setDouble(3, projet.getCoutTotal());
            pstmt.setString(4, projet.getEtatProjet().name());
            pstmt.setInt(5, projet.getClient().getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<Projet> findByNom(String nomProjet) {
        String sql = "SELECT * FROM projets WHERE nom_projet = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nomProjet);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Projet projet = new Projet(rs.getString("nom_projet"), null);
                projet.setMargeBeneficiaire(rs.getDouble("marge_beneficiaire"));
                projet.setCoutTotal(rs.getDouble("cout_total"));
                projet.setEtatProjet(EtatProjet.valueOf(rs.getString("etat_projet")));
                return Optional.of(projet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Projet> findAll() {
        List<Projet> projets = new ArrayList<>();
        String sql = "SELECT * FROM projets";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Projet projet = new Projet(rs.getString("nom_projet"), null);
                projet.setMargeBeneficiaire(rs.getDouble("marge_beneficiaire"));
                projet.setCoutTotal(rs.getDouble("cout_total"));
                projet.setEtatProjet(EtatProjet.valueOf(rs.getString("etat_projet")));
                projets.add(projet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projets;
    }
}