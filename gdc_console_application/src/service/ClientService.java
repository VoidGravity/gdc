package com.baticuisine.service;

import com.baticuisine.dao.ClientDAO;
import com.baticuisine.model.Client;
import java.util.List;
import java.util.Optional;

public class ClientService {
    private final ClientDAO clientDAO;

    public ClientService(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
    }

    public void ajouterClient(Client client) {
        clientDAO.save(client);
    }

    public Optional<Client> rechercherClientParNom(String nom) {
        return clientDAO.findByNom(nom);
    }

    public List<Client> obtenirTousLesClients() {
        return clientDAO.findAll();
    }

    public void mettreAJourClient(Client client) {
        clientDAO.save(client);
    }
}