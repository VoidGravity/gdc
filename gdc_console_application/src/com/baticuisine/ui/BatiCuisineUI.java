package com.baticuisine.ui;

import com.baticuisine.service.ClientService;
import com.baticuisine.service.ProjetService;
import com.baticuisine.model.*;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
//package com.baticuisine.ui;
//
//import com.baticuisine.service.ClientService;
//import com.baticuisine.service.ProjetService;
//import com.baticuisine.model.*;
//
//import java.util.Scanner;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.List;

public class BatiCuisineUI {
    private final ClientService clientService;
    private final ProjetService projetService;
    private final Scanner scanner;

    public BatiCuisineUI(ClientService clientService, ProjetService projetService) {
        this.clientService = clientService;
        this.projetService = projetService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("=== Menu Principal ===");
            System.out.println("1. Gérer les clients");
            System.out.println("2. Gérer les projets");
            System.out.println("3. Quitter");
            System.out.print("Choisissez une option : ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    gererClients();
                    break;
                case 2:
                    gererProjets();
                    break;
                case 3:
                    System.out.println("Au revoir!");
                    return;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    private void gererClients() {
        while (true) {
            System.out.println("=== Gestion des Clients ===");
            System.out.println("1. Ajouter un client");
            System.out.println("2. Rechercher un client");
            System.out.println("3. Afficher tous les clients");
            System.out.println("4. Retour au menu principal");
            System.out.print("Choisissez une option : ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    ajouterClient();
                    break;
                case 2:
                    rechercherClient();
                    break;
                case 3:
                    afficherTousClients();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    private void gererProjets() {
        while (true) {
            System.out.println("=== Gestion des Projets ===");
            System.out.println("1. Créer un nouveau projet");
            System.out.println("2. Ajouter un composant à un projet");
            System.out.println("3. Calculer le coût total d'un projet");
            System.out.println("4. Générer un devis");
            System.out.println("5. Afficher tous les projets");
            System.out.println("6. Retour au menu principal");
            System.out.print("Choisissez une option : ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    creerProjet();
                    break;
                case 2:
                    ajouterComposant();
                    break;
                case 3:
                    calculerCoutTotal();
                    break;
                case 4:
                    genererDevis();
                    break;
                case 5:
                    afficherTousProjets();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    private void ajouterClient() {
        System.out.print("Nom du client : ");
        String nom = scanner.nextLine();
        System.out.print("Adresse du client : ");
        String adresse = scanner.nextLine();
        System.out.print("Téléphone du client : ");
        String telephone = scanner.nextLine();
        System.out.print("Client professionnel ? (oui/non) : ");
        boolean estProfessionnel = scanner.nextLine().equalsIgnoreCase("oui");

        Client client = new Client(nom, adresse, telephone, estProfessionnel);
        clientService.ajouterClient(client);
        System.out.println("Client ajouté avec succès!");
    }

    private void rechercherClient() {
        System.out.print("Nom du client à rechercher : ");
        String nom = scanner.nextLine();
        clientService.rechercherClientParNom(nom)
                .ifPresentOrElse(
                        client -> System.out.println("Client trouvé : " + client),
                        () -> System.out.println("Client non trouvé.")
                );
    }

    private void afficherTousClients() {
        List<Client> clients = clientService.obtenirTousLesClients();
        if (clients.isEmpty()) {
            System.out.println("Aucun client trouvé.");
        } else {
            for (Client client : clients) {
                System.out.println(client);
            }
        }
    }

    private void creerProjet() {
        System.out.print("Nom du projet : ");
        String nomProjet = scanner.nextLine();
        System.out.print("Nom du client : ");
        String nomClient = scanner.nextLine();

        clientService.rechercherClientParNom(nomClient).ifPresentOrElse(
                client -> {
                    projetService.creerProjet(nomProjet, client);
                    System.out.println("Projet créé avec succès!");
                },
                () -> System.out.println("Client non trouvé. Veuillez d'abord créer le client.")
        );
    }

    private void ajouterComposant() {
        System.out.print("Nom du projet : ");
        String nomProjet = scanner.nextLine();
        System.out.println("Type de composant (1: Matériel, 2: Main d'oeuvre) : ");
        int typeComposant = scanner.nextInt();
        scanner.nextLine();

        Composant composant;
        if (typeComposant == 1) {
            composant = creerMateriel();
        } else if (typeComposant == 2) {
            composant = creerMainOeuvre();
        } else {
            System.out.println("Type de composant invalide.");
            return;
        }

        projetService.ajouterComposant(nomProjet, composant);
        System.out.println("Composant ajouté avec succès!");
    }

    private Materiel creerMateriel() {
        System.out.print("Nom du matériel : ");
        String nom = scanner.nextLine();
        System.out.print("Coût unitaire : ");
        double coutUnitaire = scanner.nextDouble();
        System.out.print("Quantité : ");
        double quantite = scanner.nextDouble();
        System.out.print("Taux de TVA : ");
        double tauxTVA = scanner.nextDouble();
        System.out.print("Coût de transport : ");
        double coutTransport = scanner.nextDouble();
        System.out.print("Coefficient de qualité : ");
        double coefficientQualite = scanner.nextDouble();

        return new Materiel(nom, coutUnitaire, quantite, tauxTVA, coutTransport, coefficientQualite);
    }

    private MainOeuvre creerMainOeuvre() {
        System.out.print("Nom de la main d'oeuvre : ");
        String nom = scanner.nextLine();
        System.out.print("Taux horaire : ");
        double tauxHoraire = scanner.nextDouble();
        System.out.print("Heures de travail : ");
        double heuresTravail = scanner.nextDouble();
        System.out.print("Taux de TVA : ");
        double tauxTVA = scanner.nextDouble();
        System.out.print("Productivité de l'ouvrier : ");
        double productiviteOuvrier = scanner.nextDouble();

        return new MainOeuvre(nom, tauxHoraire, heuresTravail, tauxTVA, productiviteOuvrier);
    }

    private void calculerCoutTotal() {
        System.out.print("Nom du projet : ");
        String nomProjet = scanner.nextLine();
        projetService.calculerCoutTotal(nomProjet);
        System.out.println("Coût total calculé avec succès!");
    }

    private void genererDevis() {
        System.out.print("Nom du projet : ");
        String nomProjet = scanner.nextLine();
        System.out.print("Date de validité du devis (format JJ/MM/AAAA) : ");
        String dateValiditeStr = scanner.nextLine();
        LocalDate dateValidite = LocalDate.parse(dateValiditeStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        projetService.genererDevis(nomProjet, dateValidite);
        System.out.println("Devis généré avec succès!");
    }

    private void afficherTousProjets() {
        List<Projet> projets = projetService.obtenirTousProjets();
        if (projets.isEmpty()) {
            System.out.println("Aucun projet trouvé.");
        } else {
            for (Projet projet : projets) {
                System.out.println(projet);
            }
        }
    }
}