package edu.bu.cs673.stockportfolio.service.portfolio;

import edu.bu.cs673.stockportfolio.domain.investment.analysts.AnalystRecommendation;
import edu.bu.cs673.stockportfolio.domain.investment.analysts.AnalystRecommendationRepository;
import edu.bu.cs673.stockportfolio.domain.investment.quote.Quote;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalystRecommendationService {

    private final AnalystRecommendationRepository analystRecommendationRepository;

    public AnalystRecommendationService(AnalystRecommendationRepository analystRecommendationRepository) {
        this.analystRecommendationRepository = analystRecommendationRepository;
    }

    /**
     * Either creates or updates an analysts recommendation based on prior existence of the symbol.
     * @param analystRecommendation The consensus estimates for a given symbol.
     */
    public void save(AnalystRecommendation analystRecommendation) {
        if (!contains(analystRecommendation.getSymbol())) {
            analystRecommendationRepository.save(analystRecommendation);
        } else {

            AnalystRecommendation existingRecommendation = analystRecommendationRepository
                    .findAnalystRecommendationBySymbol(analystRecommendation
                            .getSymbol());

            existingRecommendation
                    .setMarketConsensus(analystRecommendation
                            .getMarketConsensus());

            existingRecommendation
                    .setMarketConsensusTargetPrice(analystRecommendation
                            .getMarketConsensusTargetPrice());
        }
    }

    /**
     *
     * @param symbol The ticker symbol attached to the analysts recommendation
     * @return True if the symbol already exists in the repository; Otherwise false
     */
    public boolean contains(String symbol) {
        List<AnalystRecommendation> analystRecommendations = analystRecommendationRepository.findAll();

        for (AnalystRecommendation analystRecommendation : analystRecommendations) {
            if (analystRecommendation.getSymbol().equals(symbol)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Link a list of quotes to the associated company and vice versa.
     *
     * @param quotes List of quotes to link.
     */
    public void doLinkQuotes(List<Quote> quotes) {
        AnalystRecommendation analystRecommendation;
        for (Quote quote : quotes) {
            analystRecommendation = getBySymbol(quote.getSymbol());
            if (analystRecommendation != null) {
                quote.setAnalystRecommendation(analystRecommendation);
                analystRecommendation.getQuotes().add(quote);
            }
        }
    }

    /**
     * Returns the analyst recommendation by the symbol.
     *
     * @param symbol The ticker symbol associated with an AnalystRecommendation.
     * @return A handle to the AnalystRecommendation for the specified symbol.
     */
    public AnalystRecommendation getBySymbol(String symbol) {
        List<AnalystRecommendation> analystRecommendations = analystRecommendationRepository.findAll();
        for (AnalystRecommendation analystRecommendation : analystRecommendations) {
            if (analystRecommendation.getSymbol().equals(symbol)) {
                return analystRecommendation;
            }
        }

        return null;
    }
}
