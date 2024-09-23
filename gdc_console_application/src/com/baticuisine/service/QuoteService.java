package com.baticuisine.service;

import com.baticuisine.model.Quote;
import com.baticuisine.model.Project;
import com.baticuisine.model.Component;
import com.baticuisine.repository.QuoteRepository;
import com.baticuisine.repository.ProjectRepository;
import com.baticuisine.repository.ComponentRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class QuoteService {
    private final QuoteRepository quoteRepository;
    private final ProjectRepository projectRepository;
    private final ComponentRepository componentRepository;
    private static final String EXPORT_DIRECTORY = "C:\\Users\\YouCode\\Desktop\\devis";

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


    public void exportQuoteToPDF(int quoteId) {
        Optional<Quote> quoteOpt = quoteRepository.findById(quoteId);
        if (quoteOpt.isPresent()) {
            Quote quote = quoteOpt.get();
            String fileName = "devis_" + quoteId + ".pdf";
            String filePath = Paths.get(EXPORT_DIRECTORY, fileName).toString();

            try (PDDocument document = new PDDocument()) {
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    // Title
                    writeText(contentStream, "Devis #" + quote.getId(), 50, 700, 12, true);

                    // Details
                    float y = 680;
                    writeText(contentStream, "Date d'émission: " + quote.getDateEmission(), 50, y, 10, false);
                    y -= 15;
                    writeText(contentStream, "Date de validité: " + quote.getDateValidite(), 50, y, 10, false);
                    y -= 15;
                    writeText(contentStream, "Montant estimé: " + quote.getMontantEstime() + " €", 50, y, 10, false);
                    y -= 15;
                    writeText(contentStream, "Statut: " + (quote.isAccepte() ? "Accepté" : "En attente"), 50, y, 10, false);

                    if (quote.getProject() != null) {
                        y -= 20;
                        writeText(contentStream, "Projet: " + quote.getProject().getNomProjet(), 50, y, 10, false);
                    }
                }

                Files.createDirectories(Paths.get(EXPORT_DIRECTORY));
                document.save(filePath);
                System.out.println("Devis exporté avec succès vers: " + filePath);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors de l'exportation du devis en PDF", e);
            }
        } else {
            throw new IllegalArgumentException("Devis non trouvé avec l'ID: " + quoteId);
        }
    }

    private void writeText(PDPageContentStream contentStream, String text, float x, float y, int fontSize, boolean isBold) throws IOException {
        contentStream.beginText();
        contentStream.newLineAtOffset(x, y);
        contentStream.setFont(isBold ? new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD) : new PDType1Font(Standard14Fonts.FontName.HELVETICA), fontSize);
        contentStream.showText(text);
        contentStream.endText();
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