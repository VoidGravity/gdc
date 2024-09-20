package com.baticuisine.repository;

import com.baticuisine.model.Quote;
import java.util.List;
import java.util.Optional;

public interface QuoteRepository {
    void save(Quote quote);
    Optional<Quote> findById(int id);
    List<Quote> findAll();
    List<Quote> findByProjectId(int projectId);
    void update(Quote quote);
    void delete(int id);
}