package com.baticuisine.service;

import com.baticuisine.model.Quote;
import com.baticuisine.model.Project;
import com.baticuisine.model.Component;
import com.baticuisine.repository.QuoteRepository;
import com.baticuisine.repository.ProjectRepository;
import com.baticuisine.repository.ComponentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class QuoteService {
    private final QuoteRepository quoteRepository;
    private final ProjectRepository projectRepository;
    private final ComponentRepository componentRepository;

    public QuoteService(QuoteRepository quoteRepository, ProjectRepository projectRepository, ComponentRepository componentRepository) {
        this.quoteRepository = quoteRepository;
        this.projectRepository = projectRepository;
        this.componentRepository = componentRepository;
    }

    public void createQuote(int projectId, LocalDate dateValidite) {
        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));

            double montantEstime = calculateEstimatedAmount(project);
            LocalDate dateEmission = LocalDate.now();

            Quote quote = new Quote(montantEstime, dateEmission, dateValidite, project);
            validateQuote(quote);

            System.out.println("Quote to be saved: " + quote);
            System.out.println("Project ID: " + quote.getProject().getId());

            quoteRepository.save(quote);
        } catch (Exception e) {
            throw new RuntimeException("Error creating quote: " + e.getMessage(), e);
        }
    }

    public Optional<Quote> getQuoteById(int id) {
        return quoteRepository.findById(id);
    }

    public List<Quote> getAllQuotes() {
        return quoteRepository.findAll();
    }

    public List<Quote> getQuotesByProjectId(int projectId) {
        return quoteRepository.findByProjectId(projectId);
    }

    public void updateQuote(Quote quote) {
        Optional<Quote> existingQuote = quoteRepository.findById(quote.getId());
        if (existingQuote.isPresent()) {
            Quote updatedQuote = existingQuote.get();
            updatedQuote.setDateValidite(quote.getDateValidite());
            // Only update other fields if they are not null or have changed
            if (quote.getMontantEstime() != 0) {
                updatedQuote.setMontantEstime(quote.getMontantEstime());
            }
            if (quote.getDateEmission() != null) {
                updatedQuote.setDateEmission(quote.getDateEmission());
            }
            updatedQuote.setAccepte(quote.isAccepte());

            validateQuote(updatedQuote);
            quoteRepository.update(updatedQuote);
        } else {
            throw new IllegalArgumentException("Quote not found with ID: " + quote.getId());
        }
    }

    public void deleteQuote(int id) {
        quoteRepository.delete(id);
    }

    private void validateQuote(Quote quote) {
        if (quote.getProject() == null) {
            throw new IllegalArgumentException("Le devis doit être associé à un projet. ID du devis: " + quote.getId());
        }
        if (quote.getDateEmission() == null) {
            throw new IllegalArgumentException("La date d'émission ne peut pas être nulle");
        }
        if (quote.getDateValidite() == null) {
            throw new IllegalArgumentException("La date de validité ne peut pas être nulle");
        }
        if (quote.getDateValidite().isBefore(quote.getDateEmission())) {
            throw new IllegalArgumentException("La date de validité ne peut pas être antérieure à la date d'émission");
        }
    }

    private double calculateEstimatedAmount(Project project) {
        List<Component> components = componentRepository.findByProjectId(project.getId());
        double totalComponentCost = components.stream()
                .mapToDouble(Component::calculateCost)
                .sum();
        return totalComponentCost * (1 + project.getMargeBeneficiaire());
    }

    public void acceptQuote(int quoteId) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new IllegalArgumentException("Devis non trouvé avec l'ID : " + quoteId));
        quote.setAccepte(true);
        quoteRepository.update(quote);
    }

    public boolean isQuoteValid(Quote quote) {
        return !LocalDate.now().isAfter(quote.getDateValidite());
    }

    public void extendQuoteValidity(int quoteId, LocalDate newValidityDate) {
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new IllegalArgumentException("Devis non trouvé avec l'ID : " + quoteId));
        if (newValidityDate.isAfter(quote.getDateValidite())) {
            quote.setDateValidite(newValidityDate);
            quoteRepository.update(quote);
        } else {
            throw new IllegalArgumentException("La nouvelle date de validité doit être postérieure à l'actuelle");
        }
    }

    public double calculateTotalQuoteAmount(int projectId) {
        List<Quote> quotes = quoteRepository.findByProjectId(projectId);
        return quotes.stream()
                .mapToDouble(Quote::getMontantEstime)
                .sum();
    }
}