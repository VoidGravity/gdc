package com.baticuisine.dao;

import com.baticuisine.model.Project;
import com.baticuisine.model.Quote;
import com.baticuisine.repository.QuoteRepository;
import com.baticuisine.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuoteDAO implements QuoteRepository {
    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void save(Quote quote) {
        String sql = "INSERT INTO quotes (montant_estime, date_emission, date_validite, accepte, project_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setDouble(1, quote.getMontantEstime());
            pstmt.setDate(2, Date.valueOf(quote.getDateEmission()));
            pstmt.setDate(3, Date.valueOf(quote.getDateValidite()));
            pstmt.setBoolean(4, quote.isAccepte());
            pstmt.setInt(5, quote.getProject().getId());

            System.out.println("Executing SQL: " + sql);
            System.out.println("Parameters: " + quote.getMontantEstime() + ", " + quote.getDateEmission() + ", " +
                    quote.getDateValidite() + ", " + quote.isAccepte() + ", " + quote.getProject().getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating quote failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    quote.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating quote failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving quote: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Quote> findById(int id) {
        String sql = "SELECT q.*, p.id as project_id, p.nom_projet FROM quotes q " +
                "LEFT JOIN projects p ON q.project_id = p.id " +
                "WHERE q.id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Quote quote = extractQuoteFromResultSet(rs);
                return Optional.of(quote);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding quote by ID", e);
        }
        return Optional.empty();
    }

    private Quote extractQuoteFromResultSet(ResultSet rs) throws SQLException {
        Quote quote = new Quote();
        quote.setId(rs.getInt("id"));
        quote.setMontantEstime(rs.getDouble("montant_estime"));
        quote.setDateEmission(rs.getDate("date_emission").toLocalDate());
        quote.setDateValidite(rs.getDate("date_validite").toLocalDate());
        quote.setAccepte(rs.getBoolean("accepte"));

        int projectId = rs.getInt("project_id");
        if (!rs.wasNull()) {
            Project project = new Project();
            project.setId(projectId);
            project.setNomProjet(rs.getString("nom_projet"));
            quote.setProject(project);
        }

        return quote;
    }

    @Override
    public List<Quote> findAll() {
        List<Quote> quotes = new ArrayList<>();
        String sql = "SELECT q.*, p.id as project_id, p.nom_projet FROM quotes q " +
                "LEFT JOIN projects p ON q.project_id = p.id";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                quotes.add(extractQuoteFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding all quotes", e);
        }
        return quotes;
    }

    @Override
    public List<Quote> findByProjectId(int projectId) {
        List<Quote> quotes = new ArrayList<>();
        String sql = "SELECT * FROM quotes WHERE project_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                quotes.add(extractQuoteFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding quotes by project ID", e);
        }
        return quotes;
    }

    @Override
    public void update(Quote quote) {
        String sql = "UPDATE quotes SET montant_estime = ?, date_emission = ?, date_validite = ?, accepte = ?, project_id = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, quote.getMontantEstime());
            pstmt.setDate(2, Date.valueOf(quote.getDateEmission()));
            pstmt.setDate(3, Date.valueOf(quote.getDateValidite()));
            pstmt.setBoolean(4, quote.isAccepte());
            pstmt.setInt(5, quote.getProject().getId());
            pstmt.setInt(6, quote.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating quote", e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM quotes WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting quote", e);
        }
    }


}