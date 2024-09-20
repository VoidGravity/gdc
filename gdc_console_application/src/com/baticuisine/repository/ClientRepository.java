package com.baticuisine.repository;

import com.baticuisine.model.Client;
import java.util.List;
import java.util.Optional;

public interface ClientRepository {
    void save(Client client);
    Optional<Client> findById(int id);
    List<Client> findAll();
    void update(Client client);
    void delete(int id);
}