package com.baticuisine.ui;

import com.baticuisine.model.*;
import com.baticuisine.service.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class BatiCuisineUI {
    private final ClientService clientService;
    private final ProjectService projectService;
    private final ComponentService componentService;
    private final QuoteService quoteService;
    private final Scanner scanner;

    public BatiCuisineUI(ClientService clientService, ProjectService projectService, ComponentService componentService, QuoteService quoteService) {
        this.clientService = clientService;
        this.projectService = projectService;
        this.componentService = componentService;
        this.quoteService = quoteService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            displayMainMenu();
            int choice = getIntInput("Choisissez une option : ");
            switch (choice) {
                case 1:
                    gererClients();
                    break;
                case 2:
                    gererProjets();
                    break;
                case 3:
                    gererComposants();
                    break;
                case 4:
                    gererDevis();
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
        System.out.println("Merci d'avoir utilisé BatiCuisine. Au revoir !");
    }

    private void displayMainMenu() {
        System.out.println("\n--- Menu Principal BatiCuisine ---");
        System.out.println("1. Gérer les clients");
        System.out.println("2. Gérer les projets");
        System.out.println("3. Gérer les composants");
        System.out.println("4. Gérer les devis");
        System.out.println("5. Quitter");
    }

    private void gererClients() {
        boolean managing = true;
        while (managing) {
            System.out.println("\n--- Gestion des Clients ---");
            System.out.println("1. Ajouter un client");
            System.out.println("2. Afficher tous les clients");
            System.out.println("3. Modifier un client");
            System.out.println("4. Supprimer un client");
            System.out.println("5. Retour au menu principal");

            int choice = getIntInput("Choisissez une option : ");
            switch (choice) {
                case 1:
                    ajouterClient();
                    break;
                case 2:
                    afficherTousLesClients();
                    break;
                case 3:
                    modifierClient();
                    break;
                case 4:
                    supprimerClient();
                    break;
                case 5:
                    managing = false;
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    private void ajouterClient() {
        System.out.println("\nAjout d'un nouveau client");
        String nom = getStringInput("Nom : ");
        String adresse = getStringInput("Adresse : ");
        String telephone = getStringInput("Téléphone : ");
        boolean estProfessionnel = getBooleanInput("Est-ce un client professionnel ? (oui/non) : ");

        Client newClient = new Client(nom, adresse, telephone, estProfessionnel);
        clientService.createClient(newClient);
        System.out.println("Client ajouté avec succès.");
    }

    private void afficherTousLesClients() {
        List<Client> clients = clientService.getAllClients();
        if (clients.isEmpty()) {
            System.out.println("Aucun client trouvé.");
        } else {
            System.out.println("\nListe des clients :");
            for (Client client : clients) {
                System.out.println(client);
            }
        }
    }

    private void modifierClient() {
        int id = getIntInput("Entrez l'ID du client à modifier : ");
        Optional<Client> clientOpt = clientService.getClientById(id);
        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            System.out.println("Client actuel : " + client);

            String nom = getStringInput("Nouveau nom (laisser vide pour ne pas changer) : ");
            String adresse = getStringInput("Nouvelle adresse (laisser vide pour ne pas changer) : ");
            String telephone = getStringInput("Nouveau téléphone (laisser vide pour ne pas changer) : ");
            String estProfessionnelStr = getStringInput("Est-ce un client professionnel ? (oui/non/laisser vide pour ne pas changer) : ");

            if (!nom.isEmpty()) client.setNom(nom);
            if (!adresse.isEmpty()) client.setAdresse(adresse);
            if (!telephone.isEmpty()) client.setTelephone(telephone);
            if (!estProfessionnelStr.isEmpty()) {
                client.setEstProfessionnel(estProfessionnelStr.equalsIgnoreCase("oui"));
            }

            clientService.updateClient(client);
            System.out.println("Client mis à jour avec succès.");
        } else {
            System.out.println("Client non trouvé.");
        }
    }

    private void supprimerClient() {
        int id = getIntInput("Entrez l'ID du client à supprimer : ");
        Optional<Client> clientOpt = clientService.getClientById(id);
        if (clientOpt.isPresent()) {
            clientService.deleteClient(id);
            System.out.println("Client supprimé avec succès.");
        } else {
            System.out.println("Client non trouvé.");
        }
    }

    private void gererProjets() {
        boolean managing = true;
        while (managing) {
            System.out.println("\n--- Gestion des Projets ---");
            System.out.println("1. Créer un nouveau projet");
            System.out.println("2. Afficher tous les projets");
            System.out.println("3. Modifier un projet");
            System.out.println("4. Supprimer un projet");
            System.out.println("5. Ajouter un composant à un projet");
            System.out.println("6. Retour au menu principal");

            int choice = getIntInput("Choisissez une option : ");
            switch (choice) {
                case 1:
                    creerProjet();
                    break;
                case 2:
                    afficherTousLesProjets();
                    break;
                case 3:
                    modifierProjet();
                    break;
                case 4:
                    supprimerProjet();
                    break;
                case 5:
                    ajouterComposantAuProjet();
                    break;
                case 6:
                    managing = false;
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    private void creerProjet() {
        System.out.println("\nCréation d'un nouveau projet");
        String nomProjet = getStringInput("Nom du projet : ");
        double margeBeneficiaire = getDoubleInput("Marge bénéficiaire (en pourcentage) : ") / 100.0;
        int clientId = getIntInput("ID du client associé : ");

        Optional<Client> clientOpt = clientService.getClientById(clientId);
        if (clientOpt.isPresent()) {
            Project newProject = new Project(nomProjet, margeBeneficiaire, clientOpt.get());
            projectService.createProject(newProject);
            System.out.println("Projet créé avec succès.");
        } else {
            System.out.println("Client non trouvé. Création du projet annulée.");
        }
    }

    private void afficherTousLesProjets() {
        List<Project> projects = projectService.getAllProjects();
        if (projects.isEmpty()) {
            System.out.println("Aucun projet trouvé.");
        } else {
            System.out.println("\nListe des projets :");
            for (Project project : projects) {
                System.out.println(project);
                System.out.println("Coût total : " + projectService.calculateTotalCost(project.getId()));
                System.out.println("--------------------");
            }
        }
    }

    private void modifierProjet() {
        int id = getIntInput("Entrez l'ID du projet à modifier : ");
        Optional<Project> projectOpt = projectService.getProjectById(id);
        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            System.out.println("Projet actuel : " + project);

            String nomProjet = getStringInput("Nouveau nom du projet (laisser vide pour ne pas changer) : ");
            String margeBeneficiaireStr = getStringInput("Nouvelle marge bénéficiaire en pourcentage (laisser vide pour ne pas changer) : ");
            String etatProjetStr = getStringInput("Nouvel état du projet (EN_COURS, TERMINE, ANNULE, laisser vide pour ne pas changer) : ");

            if (!nomProjet.isEmpty()) project.setNomProjet(nomProjet);
            if (!margeBeneficiaireStr.isEmpty()) {
                project.setMargeBeneficiaire(Double.parseDouble(margeBeneficiaireStr) / 100.0);
            }
            if (!etatProjetStr.isEmpty()) {
                project.setEtatProjet(EtatProjet.valueOf(etatProjetStr.toUpperCase()));
            }

            projectService.updateProject(project);
            System.out.println("Projet mis à jour avec succès.");
        } else {
            System.out.println("Projet non trouvé.");
        }
    }

    private void supprimerProjet() {
        int id = getIntInput("Entrez l'ID du projet à supprimer : ");
        Optional<Project> projectOpt = projectService.getProjectById(id);
        if (projectOpt.isPresent()) {
            projectService.deleteProject(id);
            System.out.println("Projet supprimé avec succès.");
        } else {
            System.out.println("Projet non trouvé.");
        }
    }

    private void ajouterComposantAuProjet() {
        int projectId = getIntInput("Entrez l'ID du projet : ");
        int componentId = getIntInput("Entrez l'ID du composant à ajouter : ");

        try {
            projectService.addComponentToProject(projectId, componentId);
            System.out.println("Composant ajouté au projet avec succès.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Une erreur est survenue lors de l'ajout du composant au projet : " + e.getMessage());
        }
    }

    private double getDoubleInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.println("Veuillez entrer un nombre valide.");
            scanner.next();
        }
        return scanner.nextDouble();
    }

    private void gererComposants() {
        boolean managing = true;
        while (managing) {
            System.out.println("\n--- Gestion des Composants ---");
            System.out.println("1. Ajouter un composant (Matériel)");
            System.out.println("2. Ajouter un composant (Main d'œuvre)");
            System.out.println("3. Afficher tous les composants");
            System.out.println("4. Modifier un composant");
            System.out.println("5. Supprimer un composant");
            System.out.println("6. Retour au menu principal");

            int choice = getIntInput("Choisissez une option : ");
            switch (choice) {
                case 1:
                    ajouterMateriel();
                    break;
                case 2:
                    ajouterMainDoeuvre();
                    break;
                case 3:
                    afficherTousLesComposants();
                    break;
                case 4:
                    modifierComposant();
                    break;
                case 5:
                    supprimerComposant();
                    break;
                case 6:
                    managing = false;
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    private void ajouterMateriel() {
        System.out.println("\nAjout d'un nouveau matériel");
        String nom = getStringInput("Nom : ");
        double tauxTVA = getDoubleInput("Taux de TVA (en pourcentage) : ");
        double coutUnitaire = getDoubleInput("Coût unitaire : ");
        double quantite = getDoubleInput("Quantité : ");
        double coutTransport = getDoubleInput("Coût de transport : ");
        double coefficientQualite = getDoubleInput("Coefficient de qualité : ");

        Material material = new Material(nom, tauxTVA, coutUnitaire, quantite, coutTransport, coefficientQualite);
        componentService.createComponent(material);
        System.out.println("Matériel ajouté avec succès.");
    }

    private void ajouterMainDoeuvre() {
        System.out.println("\nAjout d'une nouvelle main d'œuvre");
        String nom = getStringInput("Nom : ");
        double tauxTVA = getDoubleInput("Taux de TVA (en pourcentage) : ");
        double tauxHoraire = getDoubleInput("Taux horaire : ");
        double heuresTravail = getDoubleInput("Heures de travail : ");
        double productiviteOuvrier = getDoubleInput("Productivité de l'ouvrier : ");

        Labor labor = new Labor(nom, tauxTVA, tauxHoraire, heuresTravail, productiviteOuvrier);
        componentService.createComponent(labor);
        System.out.println("Main d'œuvre ajoutée avec succès.");
    }

    private void afficherTousLesComposants() {
        List<Component> components = componentService.getAllComponents();
        if (components.isEmpty()) {
            System.out.println("Aucun composant trouvé.");
        } else {
            System.out.println("\nListe des composants :");
            for (Component component : components) {
                System.out.println(component);
                System.out.println("Coût : " + componentService.calculateComponentCost(component));
                System.out.println("--------------------");
            }
        }
    }

    private void modifierComposant() {
        int id = getIntInput("Entrez l'ID du composant à modifier : ");
        Optional<Component> componentOpt = componentService.getComponentById(id);
        if (componentOpt.isPresent()) {
            Component component = componentOpt.get();
            System.out.println("Composant actuel : " + component);

            String nom = getStringInput("Nouveau nom (laisser vide pour ne pas changer) : ");
            String tauxTVAStr = getStringInput("Nouveau taux de TVA en pourcentage (laisser vide pour ne pas changer) : ");

            if (!nom.isEmpty()) component.setNom(nom);
            if (!tauxTVAStr.isEmpty()) component.setTauxTVA(Double.parseDouble(tauxTVAStr));

            if (component instanceof Material) {
                modifierMateriel((Material) component);
            } else if (component instanceof Labor) {
                modifierMainDoeuvre((Labor) component);
            }

            componentService.updateComponent(component);
            System.out.println("Composant mis à jour avec succès.");
        } else {
            System.out.println("Composant non trouvé.");
        }
    }

    private void modifierMateriel(Material material) {
        String coutUnitaireStr = getStringInput("Nouveau coût unitaire (laisser vide pour ne pas changer) : ");
        String quantiteStr = getStringInput("Nouvelle quantité (laisser vide pour ne pas changer) : ");
        String coutTransportStr = getStringInput("Nouveau coût de transport (laisser vide pour ne pas changer) : ");
        String coefficientQualiteStr = getStringInput("Nouveau coefficient de qualité (laisser vide pour ne pas changer) : ");

        if (!coutUnitaireStr.isEmpty()) material.setCoutUnitaire(Double.parseDouble(coutUnitaireStr));
        if (!quantiteStr.isEmpty()) material.setQuantite(Double.parseDouble(quantiteStr));
        if (!coutTransportStr.isEmpty()) material.setCoutTransport(Double.parseDouble(coutTransportStr));
        if (!coefficientQualiteStr.isEmpty()) material.setCoefficientQualite(Double.parseDouble(coefficientQualiteStr));
    }

    private void modifierMainDoeuvre(Labor labor) {
        String tauxHoraireStr = getStringInput("Nouveau taux horaire (laisser vide pour ne pas changer) : ");
        String heuresTravailStr = getStringInput("Nouvelles heures de travail (laisser vide pour ne pas changer) : ");
        String productiviteOuvrierStr = getStringInput("Nouvelle productivité de l'ouvrier (laisser vide pour ne pas changer) : ");

        if (!tauxHoraireStr.isEmpty()) labor.setTauxHoraire(Double.parseDouble(tauxHoraireStr));
        if (!heuresTravailStr.isEmpty()) labor.setHeuresTravail(Double.parseDouble(heuresTravailStr));
        if (!productiviteOuvrierStr.isEmpty()) labor.setProductiviteOuvrier(Double.parseDouble(productiviteOuvrierStr));
    }

    private void supprimerComposant() {
        int id = getIntInput("Entrez l'ID du composant à supprimer : ");
        Optional<Component> componentOpt = componentService.getComponentById(id);
        if (componentOpt.isPresent()) {
            componentService.deleteComponent(id);
            System.out.println("Composant supprimé avec succès.");
        } else {
            System.out.println("Composant non trouvé.");
        }
    }

    private void gererDevis() {
        boolean managing = true;
        while (managing) {
            System.out.println("\n--- Gestion des Devis ---");
            System.out.println("1. Créer un nouveau devis");
            System.out.println("2. Afficher tous les devis");
            System.out.println("3. Modifier un devis");
            System.out.println("4. Supprimer un devis");
            System.out.println("5. Accepter un devis");
            System.out.println("6. Prolonger la validité d'un devis");
            System.out.println("7. Retour au menu principal");

            int choice = getIntInput("Choisissez une option : ");
            switch (choice) {
                case 1:
                    creerDevis();
                    break;
                case 2:
                    afficherTousLesDevis();
                    break;
                case 3:
                    modifierDevis();
                    break;
                case 4:
                    supprimerDevis();
                    break;
                case 5:
                    accepterDevis();
                    break;
                case 6:
                    prolongerValiditeDevis();
                    break;
                case 7:
                    managing = false;
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    private void creerDevis() {
        System.out.println("\nCréation d'un nouveau devis");
        int projectId = getIntInput("ID du projet associé : ");
        Optional<Project> projectOpt = projectService.getProjectById(projectId);

        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            LocalDate dateEmission = LocalDate.now();
            LocalDate dateValidite = getDateInput("Date de validité (JJ/MM/AAAA) : ");

            Quote newQuote = new Quote(0, dateEmission, dateValidite, project);
            quoteService.createQuote(newQuote);
            System.out.println("Devis créé avec succès. Montant estimé : " + newQuote.getMontantEstime());
        } else {
            System.out.println("Projet non trouvé. Création du devis annulée.");
        }
    }

    private void afficherTousLesDevis() {
        List<Quote> quotes = quoteService.getAllQuotes();
        if (quotes.isEmpty()) {
            System.out.println("Aucun devis trouvé.");
        } else {
            System.out.println("\nListe des devis :");
            for (Quote quote : quotes) {
                System.out.println(quote);
                System.out.println("Validité : " + (quoteService.isQuoteValid(quote) ? "Valide" : "Expiré"));
                System.out.println("--------------------");
            }
        }
    }

    private void modifierDevis() {
        int id = getIntInput("Entrez l'ID du devis à modifier : ");
        Optional<Quote> quoteOpt = quoteService.getQuoteById(id);
        if (quoteOpt.isPresent()) {
            Quote quote = quoteOpt.get();
            System.out.println("Devis actuel : " + quote);

            LocalDate newDateValidite = getDateInput("Nouvelle date de validité (JJ/MM/AAAA, laisser vide pour ne pas changer) : ");
            if (newDateValidite != null) {
                quote.setDateValidite(newDateValidite);
            }

            quoteService.updateQuote(quote);
            System.out.println("Devis mis à jour avec succès.");
        } else {
            System.out.println("Devis non trouvé.");
        }
    }

    private void supprimerDevis() {
        int id = getIntInput("Entrez l'ID du devis à supprimer : ");
        Optional<Quote> quoteOpt = quoteService.getQuoteById(id);
        if (quoteOpt.isPresent()) {
            quoteService.deleteQuote(id);
            System.out.println("Devis supprimé avec succès.");
        } else {
            System.out.println("Devis non trouvé.");
        }
    }

    private void accepterDevis() {
        int id = getIntInput("Entrez l'ID du devis à accepter : ");
        try {
            quoteService.acceptQuote(id);
            System.out.println("Devis accepté avec succès.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private void prolongerValiditeDevis() {
        int id = getIntInput("Entrez l'ID du devis à prolonger : ");
        LocalDate newValidityDate = getDateInput("Nouvelle date de validité (JJ/MM/AAAA) : ");
        try {
            quoteService.extendQuoteValidity(id, newValidityDate);
            System.out.println("Validité du devis prolongée avec succès.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    private LocalDate getDateInput(String prompt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        while (true) {
            try {
                System.out.print(prompt);
                String dateStr = scanner.next().trim();
                if (dateStr.isEmpty()) {
                    return null;
                }
                return LocalDate.parse(dateStr, formatter);
            } catch (Exception e) {
                System.out.println("Format de date invalide. Utilisez le format JJ/MM/AAAA.");
            }
        }
    }

    private int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println("Veuillez entrer un nombre valide.");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.next().trim();
    }

    private boolean getBooleanInput(String prompt) {
        System.out.print(prompt);
        String input = scanner.next().trim().toLowerCase();
        while (!input.equals("oui") && !input.equals("non")) {
            System.out.println("Veuillez répondre par 'oui' ou 'non'.");
            System.out.print(prompt);
            input = scanner.next().trim().toLowerCase();
        }
        return input.equals("oui");
    }


}