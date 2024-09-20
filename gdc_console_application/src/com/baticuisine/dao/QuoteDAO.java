package com.baticuisine.dao;

import com.baticuisine.model.Quote;
import com.baticuisine.repository.QuoteRepository;
import com.baticuisine.db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuoteDAO implements QuoteRepository {
    private Connection connection;

    public QuoteDAO() {
        try {
            this.connection = DatabaseConnection.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }

    @Override
    public void save(Quote quote) {
        String sql = "INSERT INTO quotes (montant_estime, date_emission, date_validite, accepte, project_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDouble(1, quote.getMontantEstime());
            pstmt.setDate(2, Date.valueOf(quote.getDateEmission()));
            pstmt.setDate(3, Date.valueOf(quote.getDateValidite()));
            pstmt.setBoolean(4, quote.isAccepte());
            pstmt.setInt(5, quote.getProject().getId());

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
            throw new RuntimeException("Error saving quote", e);
        }
    }

    @Override
    public Optional<Quote> findById(int id) {
        String sql = "SELECT * FROM quotes WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(extractQuoteFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding quote by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Quote> findAll() {
        List<Quote> quotes = new ArrayList<>();
        String sql = "SELECT * FROM quotes";
        try (Statement stmt = connection.createStatement();
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
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
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
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
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
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting quote", e);
        }
    }

    private Quote extractQuoteFromResultSet(ResultSet rs) throws SQLException {
        Quote quote = new Quote();
        quote.setId(rs.getInt("id"));
        quote.setMontantEstime(rs.getDouble("montant_estime"));
        quote.setDateEmission(rs.getDate("date_emission").toLocalDate());
        quote.setDateValidite(rs.getDate("date_validite").toLocalDate());
        quote.setAccepte(rs.getBoolean("accepte"));
        return quote;
    }
}