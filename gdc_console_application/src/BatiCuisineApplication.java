package com.baticuisine;

import com.baticuisine.dao.ClientDAO;
import com.baticuisine.dao.ProjetDAO;
import com.baticuisine.service.ClientService;
import com.baticuisine.service.ProjetService;
import com.baticuisine.ui.BatiCuisineUI;

public class BatiCuisineApplication {
    public static void main(String[] args) {
        ClientDAO clientDAO = new ClientDAO();
        ProjetDAO projetDAO = new ProjetDAO();

        ClientService clientService = new ClientService(clientDAO);
        ProjetService projetService = new ProjetService(projetDAO);

        BatiCuisineUI ui = new BatiCuisineUI(clientService, projetService);
        ui.start();
    }
}