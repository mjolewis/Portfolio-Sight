package edu.bu.cs673.stockportfolio.marketdata.model.quote.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuoteRepository extends JpaRepository<Quote, Long> {
    List<Quote> findQuotesBySymbol(String symbol);

    Quote findQuoteBySymbol(String symbol);
}
