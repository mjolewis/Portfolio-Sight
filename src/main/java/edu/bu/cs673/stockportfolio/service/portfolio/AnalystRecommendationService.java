package edu.bu.cs673.stockportfolio.service.portfolio;

import edu.bu.cs673.stockportfolio.domain.investment.analysts.AnalystRecommendation;
import edu.bu.cs673.stockportfolio.domain.investment.analysts.AnalystRecommendationRepository;
import edu.bu.cs673.stockportfolio.domain.investment.quote.Quote;
import org.fissore.slf4j.FluentLogger;
import org.fissore.slf4j.FluentLoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class AnalystRecommendationService {

    private static final FluentLogger LOGGER = FluentLoggerFactory.getLogger(AnalystRecommendation.class);
    private final AnalystRecommendationRepository analystRecommendationRepository;
    private final MarketDataService marketDataService;

    public AnalystRecommendationService(AnalystRecommendationRepository analystRecommendationRepository,
                                        MarketDataServiceImpl marketDataService) {
        this.analystRecommendationRepository = analystRecommendationRepository;
        this.marketDataService = marketDataService;
    }

    /**
     * Filter Analyst Recommendations by symbol. Existing Analyst Recommendations will be updated otherwise they will
     * be created. This is used as an optimization to avoid polluting the database with redundant data because the
     * system doesn't need to track historical Analyst Recommendations. It only cares about the current Analyst
     * Recommendation.
     *
     * @param analystRecommendations A list of all Analyst Recommendation responses from IEX Cloud.
     * @return A List of Analyst Recommendations that will be updated or created.
     */
    public List<AnalystRecommendation> getAnalystRecommendationsToBeUpdatedOrCreated(
            List<AnalystRecommendation> analystRecommendations) {

        List<AnalystRecommendation> filteredAnalystRecommendations = new ArrayList<>();

        analystRecommendations.forEach(analystRecommendation -> {
            String symbol = analystRecommendation.getSymbol();

            AnalystRecommendation existingAnalystRecommendation =
                    analystRecommendationRepository.findAnalystRecommendationBySymbol(symbol);

            if (existingAnalystRecommendation != null) {
                filteredAnalystRecommendations.add(updateExistingAnalystRecommendation(existingAnalystRecommendation));
            }
        });

        return filteredAnalystRecommendations;
    }

    // Update the Analyst Recommendation if it exists
    private AnalystRecommendation updateExistingAnalystRecommendation(AnalystRecommendation analystRecommendation) {
        analystRecommendation.setMarketConsensusTargetPrice(analystRecommendation.getMarketConsensusTargetPrice());
        analystRecommendation.setMarketConsensus(analystRecommendation.getMarketConsensus());
        return analystRecommendation;
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

    /**
     * Gets the latest Analyst Recommendations from IEXCloud for a basket of securities. This task is scheduled by the
     * AnalystRecommendationScheduler.
     */
    public void getAnalystRecommendations() {
        LOGGER.info().log("Getting latest analyst recommendations from IEX Cloud");

        List<AnalystRecommendation> existingAnalystRecommendations = analystRecommendationRepository.findAll();

        // Package the existing analyst recommendations into a set for the marketDataServiceImpl
        Set<String> allSymbols = new HashSet<>();
        existingAnalystRecommendations.forEach(analystRecommendationToBeUpdated -> {
            allSymbols.add(String.join(",", analystRecommendationToBeUpdated.getSymbol()));
        });

        // GET new analyst recommendations from IEX Cloud
        if (allSymbols.size() != 0) {
            List<AnalystRecommendation> analystRecommendations =
                    marketDataService.doGetAnalystRecommendations(allSymbols);

            analystRecommendations.forEach(analystRecommendation -> {
                existingAnalystRecommendations.forEach(existingAnalystRecommendation -> {
                    if (existingAnalystRecommendation.getSymbol().equals(analystRecommendation.getSymbol())) {

                        // Pull the existing analyst recommendation into the persistence context
                        Optional<AnalystRecommendation> optionalAnalystRecommendation =
                                analystRecommendationRepository.findById(existingAnalystRecommendation.getId());

                        if (optionalAnalystRecommendation.isPresent()) {
                            AnalystRecommendation analystRecommendationToBeUpdated =
                                    optionalAnalystRecommendation.get();

                            analystRecommendationToBeUpdated.setMarketConsensusTargetPrice(
                                    analystRecommendation.getMarketConsensusTargetPrice());

                            analystRecommendationToBeUpdated.setMarketConsensus(
                                    analystRecommendation.getMarketConsensus());
                        }
                    }
                });
            });
        }
    }
}
