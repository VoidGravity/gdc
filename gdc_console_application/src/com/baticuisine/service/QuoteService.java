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

    public void createQuote(Quote quote) {
        validateQuote(quote);
        quote.setMontantEstime(calculateEstimatedAmount(quote.getProject()));
        quoteRepository.save(quote);
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
        validateQuote(quote);
        quoteRepository.update(quote);
    }

    public void deleteQuote(int id) {
        quoteRepository.delete(id);
    }

    private void validateQuote(Quote quote) {
        if (quote.getProject() == null) {
            throw new IllegalArgumentException("Le devis doit être associé à un projet");
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
        // Add more validation as needed
    }

    private double calculateEstimatedAmount(Project project) {
        List<Component> components = componentRepository.findByProjectId(project.getId());
        double totalComponentCost = components.stream()
                .mapToDouble(Component::calculateCost)
                .sum();
        return totalComponentCost * (1 + project.getMargeBeneficiaire());
    }

    public void acceptQuote(int quoteId) {
        Optional<Quote> quoteOpt = quoteRepository.findById(quoteId);
        if (quoteOpt.isPresent()) {
            Quote quote = quoteOpt.get();
            quote.setAccepte(true);
            quoteRepository.update(quote);
        } else {
            throw new IllegalArgumentException("Devis non trouvé avec l'ID : " + quoteId);
        }
    }

    public boolean isQuoteValid(Quote quote) {
        return !LocalDate.now().isAfter(quote.getDateValidite());
    }

    public void extendQuoteValidity(int quoteId, LocalDate newValidityDate) {
        Optional<Quote> quoteOpt = quoteRepository.findById(quoteId);
        if (quoteOpt.isPresent()) {
            Quote quote = quoteOpt.get();
            if (newValidityDate.isAfter(quote.getDateValidite())) {
                quote.setDateValidite(newValidityDate);
                quoteRepository.update(quote);
            } else {
                throw new IllegalArgumentException("La nouvelle date de validité doit être postérieure à l'actuelle");
            }
        } else {
            throw new IllegalArgumentException("Devis non trouvé avec l'ID : " + quoteId);
        }
    }

    public double calculateTotalQuoteAmount(int projectId) {
        List<Quote> quotes = quoteRepository.findByProjectId(projectId);
        return quotes.stream()
                .mapToDouble(Quote::getMontantEstime)
                .sum();
    }
}