package com.baticuisine.service;

import com.baticuisine.model.Client;
import com.baticuisine.repository.ClientRepository;

import java.util.List;
import java.util.Optional;

public class ClientService {
    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public void createClient(Client client) {
        validateClient(client);
        clientRepository.save(client);
    }

    public Optional<Client> getClientById(int id) {
        return clientRepository.findById(id);
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public void updateClient(Client client) {
        validateClient(client);
        clientRepository.update(client);
    }

    public void deleteClient(int id) {
        clientRepository.delete(id);
    }

    private void validateClient(Client client) {

        if (client.getNom() == null || client.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du client ne peut pas être vide");
        }
        if (client.getAdresse() == null || client.getAdresse().trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse du client ne peut pas être vide");
        }
        if (client.getTelephone() == null || client.getTelephone().trim().isEmpty()) {
            throw new IllegalArgumentException("Le numéro de téléphone du client ne peut pas être vide");
        }

    }

    public double calculateClientDiscount(Client client) {
        if (client.isEstProfessionnel()) {
            return 0.1; // 10% discount for professional clients
        } else {
            return 0.0; // No discount for regular clients
        }
    }
}