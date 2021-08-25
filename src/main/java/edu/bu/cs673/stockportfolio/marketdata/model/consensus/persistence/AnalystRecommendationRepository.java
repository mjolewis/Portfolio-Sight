package edu.bu.cs673.stockportfolio.marketdata.model.consensus.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalystRecommendationRepository extends JpaRepository<AnalystRecommendation, Long> {

    AnalystRecommendation findAnalystRecommendationBySymbol(String symbol);
}
