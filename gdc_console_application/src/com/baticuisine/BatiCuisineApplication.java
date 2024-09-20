package com.baticuisine;

import com.baticuisine.ui.BatiCuisineUI;
import com.baticuisine.service.*;
import com.baticuisine.dao.*;

public class BatiCuisineApplication {
    public static void main(String[] args) {
        ClientDAO clientDAO = new ClientDAO();
        ProjectDAO projectDAO = new ProjectDAO();
        ComponentDAO componentDAO = new ComponentDAO();
        QuoteDAO quoteDAO = new QuoteDAO();

        ClientService clientService = new ClientService(clientDAO);
        ProjectService projectService = new ProjectService(projectDAO, componentDAO);
        ComponentService componentService = new ComponentService(componentDAO);
        QuoteService quoteService = new QuoteService(quoteDAO, projectDAO, componentDAO);

        BatiCuisineUI ui = new BatiCuisineUI(clientService, projectService, componentService, quoteService);
        ui.start();
    }
}