package com.baticuisine;

import com.baticuisine.ui.BatiCuisineUI;
import com.baticuisine.service.*;
import com.baticuisine.dao.*;

public class BatiCuisineApplication {
    public static void main(String[] args) {
        ClientService clientService = new ClientService(new ClientDAO());
        ProjectService projectService = new ProjectService(new ProjectDAO());
        ComponentService componentService = new ComponentService(new ComponentDAO());
        QuoteService quoteService = new QuoteService(new QuoteDAO());

        BatiCuisineUI ui = new BatiCuisineUI(clientService, projectService, componentService, quoteService);
        ui.start();
    }
}