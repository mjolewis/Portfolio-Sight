package edu.bu.cs673.stockportfolio.service.portfolio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.bu.cs673.stockportfolio.domain.investment.analysts.AnalystRecommendation;
import edu.bu.cs673.stockportfolio.domain.investment.analysts.AnalystRecommendationRepository;
import edu.bu.cs673.stockportfolio.domain.investment.quote.QuoteRoot;
import edu.bu.cs673.stockportfolio.domain.investment.sector.CompanyRoot;
import edu.bu.cs673.stockportfolio.service.utilities.IexCloudConfig;
import org.fissore.slf4j.FluentLogger;
import org.fissore.slf4j.FluentLoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

/**********************************************************************************************************************
 * The MarketDataServiceImpl uses a synchronous client to perform HTTP requests to IEX Cloud endpoints. It retrieves
 * representations for the products Quote and Company entities and persists them into the database.
 *********************************************************************************************************************/
@Service
@Transactional
public class MarketDataServiceImpl implements MarketDataService {

    private static final FluentLogger LOGGER = FluentLoggerFactory.getLogger(MarketDataServiceImpl.class);
    private static final String BASE_URL = "https://cloud.iexapis.com/";
    private static final String VERSION = "stable/";
    private static final String TOKEN_PARAM = "&token=";
    private final RestTemplate restTemplate;
    private final String token;
    private final AnalystRecommendationRepository analystRecommendationRepository;

    public MarketDataServiceImpl(RestTemplate restTemplate,
                                 IexCloudConfig iexCloudConfig,
                                 AnalystRecommendationRepository analystRecommendationRepository) {
        this.restTemplate = restTemplate;
        this.token = iexCloudConfig.getToken();
        this.analystRecommendationRepository = analystRecommendationRepository;
    }

    @Override
    public boolean isUSMarketOpen() {
        LOGGER.info().log("Checking if US Market is open");

        // Use a well known symbol to check if market is open.
        String symbol = "GS";
        String field = "isUSMarketOpen";
        String endpointPath = String.format("stock/%s/quote/%s?token=", symbol, field);

        Boolean isUSMarketOpen = restTemplate.getForObject(
                BASE_URL + VERSION + endpointPath + token, Boolean.class);

       return isUSMarketOpen != null && isUSMarketOpen;
    }

    @Override
    public QuoteRoot doGetQuotes(Set<String> symbols) {

        // Convert the Set of Strings to a String for batch IEX request
        String symbolFilter = String.join(",", symbols);
        String endpointPath = "stock/market/batch";
        String queryParams = String.format(
                "?symbols=%s&types=quote&filter="
                        + "companyName,"
                        + "symbol,"
                        + "latestPrice,"
                        + "marketCap",
                symbolFilter);

        LOGGER.info().log("Getting quotes from IEX Cloud for {}", symbolFilter);
        return restTemplate.getForObject(
                BASE_URL
                        + VERSION
                        + endpointPath
                        + queryParams
                        + TOKEN_PARAM
                        + token
                , QuoteRoot.class);
    }

    @Override
    public CompanyRoot doGetCompanies(Set<String> symbols) {

        String symbolFilter = String.join(",", symbols);
        String endpointPath = "stock/market/batch";
        String queryParams = String.format("?symbols=%s&types=company&filter=symbol,sector,companyName", symbolFilter);

        LOGGER.info().log("Getting company data from IEX Cloud for {}", symbolFilter);
        return restTemplate.getForObject(
                BASE_URL
                        + VERSION
                        + endpointPath
                        + queryParams
                        + TOKEN_PARAM
                        + token
                , CompanyRoot.class);
    }

    @Override
    public List<AnalystRecommendation> doGetAnalystRecommendations(Set<String> symbols) {

        List<AnalystRecommendation> analystRecommendations = new ArrayList<>();
        String queryParams = "?filter=symbol,marketConsensus,marketConsensusTargetPrice";
        symbols.forEach(symbol -> {
            LOGGER.error().log("Getting analyst recommendation for {}", symbol);

            String endpointPath = String.format("time-series/CORE_ESTIMATES/%s", symbol);
            String jsonStr = restTemplate.getForObject(
                    BASE_URL
                            + VERSION
                            + endpointPath
                            + queryParams
                            + TOKEN_PARAM
                            + token,
                    String.class);

            ObjectMapper mapper = new ObjectMapper();
            AnalystRecommendation[] analystRecommendation = null;
            try {
                analystRecommendation = mapper.readValue(jsonStr, AnalystRecommendation[].class);
            } catch (JsonProcessingException e) {
                LOGGER.error().log("JSON processing exception deserializing analyst recommendation for {}", symbol);
            }

            try {
                AnalystRecommendation analystRecommendationToBeSaved =
                        updateExistingAnalystRecommendationOrGetNewAnalystRecommendation(analystRecommendation[0]);

                analystRecommendations.add(analystRecommendationToBeSaved);
            } catch (NullPointerException | IndexOutOfBoundsException e) {
                // Fail gracefully by logging error and allow the program to continue executing
                LOGGER.error().log("Error. No analyst recommendation found for {}.", symbol);
                AnalystRecommendation failSafe = new AnalystRecommendation();
                failSafe.setSymbol(symbol);
                failSafe.setMarketConsensus(0F);
                failSafe.setMarketConsensusTargetPrice(new BigDecimal("0"));
                analystRecommendations.add(failSafe);
            }
        });

        return analystRecommendations;
    }

    // Update the quote if it exists, otherwise return the new quote
    private AnalystRecommendation updateExistingAnalystRecommendationOrGetNewAnalystRecommendation(
            AnalystRecommendation analystRecommendation) {

        String symbol = analystRecommendation.getSymbol();

        AnalystRecommendation existingAnalystRecommendation =
                analystRecommendationRepository.findAnalystRecommendationBySymbol(symbol);

        if (existingAnalystRecommendation != null) {

            existingAnalystRecommendation.setMarketConsensusTargetPrice(
                    analystRecommendation.getMarketConsensusTargetPrice());

            existingAnalystRecommendation.setMarketConsensus(analystRecommendation.getMarketConsensus());

            return existingAnalystRecommendation;
        }

        return analystRecommendation;
    }
}
