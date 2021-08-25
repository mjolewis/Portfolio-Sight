package edu.bu.cs673.stockportfolio.marketdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.bu.cs673.stockportfolio.marketdata.model.consensus.persistence.AnalystRecommendation;
import edu.bu.cs673.stockportfolio.marketdata.model.quote.persistence.QuoteRoot;
import edu.bu.cs673.stockportfolio.marketdata.model.company.persistence.CompanyRoot;
import edu.bu.cs673.stockportfolio.marketdata.config.IexCloudConfig;
import org.fissore.slf4j.FluentLogger;
import org.fissore.slf4j.FluentLoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

/**********************************************************************************************************************
 * The MarketDataServiceImpl uses a synchronous client to perform HTTP requests to IEX Cloud endpoints. It retrieves
 * representations for the products Quote, Company, and Analyst Recommendation entities.
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

    public MarketDataServiceImpl(RestTemplate restTemplate,
                                 IexCloudConfig iexCloudConfig) {
        this.restTemplate = restTemplate;
        this.token = iexCloudConfig.getToken();
    }

    @Override
    public boolean isUSMarketOpen() {
        LOGGER.info().log("Checking if US Market is open");

        // Use a well known symbol to check if market is open
        String symbol = "GS";
        String field = "isUSMarketOpen";
        String endpoint = String.format("stock/%s/quote/%s?token=", symbol, field);

        Boolean isUSMarketOpen = restTemplate.getForObject(BASE_URL + VERSION + endpoint + token, Boolean.class);

       return isUSMarketOpen != null && isUSMarketOpen;
    }

    @Override
    public QuoteRoot doGetQuotes(Set<String> symbols) {

        // Convert the Set of Strings to a String for batch IEX request
        String symbolFilter = String.join(",", symbols);
        String endpoint = "stock/market/batch";
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
                        + endpoint
                        + queryParams
                        + TOKEN_PARAM
                        + token
                , QuoteRoot.class);
    }

    @Override
    public CompanyRoot doGetCompanies(Set<String> symbols) {

        String symbolFilter = String.join(",", symbols);
        String endpoint = "stock/market/batch";
        String queryParams = String.format("?symbols=%s&types=company&filter=symbol,sector,companyName", symbolFilter);

        LOGGER.info().log("Getting company data from IEX Cloud for {}", symbolFilter);
        return restTemplate.getForObject(
                BASE_URL
                        + VERSION
                        + endpoint
                        + queryParams
                        + TOKEN_PARAM
                        + token
                , CompanyRoot.class);
    }

    @Override
    public List<AnalystRecommendation> doGetAnalystRecommendations(Set<String> symbols) {

        List<AnalystRecommendation> analystRecommendations = new ArrayList<>();
        String queryParams = "?filter=symbol,marketConsensus,marketConsensusTargetPrice";

        // This endpoint does not allow batch requests, so process the symbols one-by-one
        symbols.forEach(symbol -> {
            LOGGER.info().log("Getting analyst recommendation for {}", symbol);

            String endpointPath = String.format("time-series/CORE_ESTIMATES/%s", symbol);
            String jsonStr = restTemplate.getForObject(
                    BASE_URL
                            + VERSION
                            + endpointPath
                            + queryParams
                            + TOKEN_PARAM
                            + token,
                    String.class);

            analystRecommendations.add(processAnalystRecommendationResponse(jsonStr, symbol));
        });

        return analystRecommendations;
    }

    // Maps the IEX Cloud response into an AnalystRecommendation object for a specific symbol
    private AnalystRecommendation processAnalystRecommendationResponse(String jsonStr, String symbol) {

        AnalystRecommendation[] analystRecommendation;
        ObjectMapper mapper = new ObjectMapper();          // Maps the IEX Cloud response into a POJO
        try {
            analystRecommendation = mapper.readValue(jsonStr, AnalystRecommendation[].class);
            if (doesAnalystRecommendationExist(analystRecommendation)) {
                return analystRecommendation[0];
            }

            LOGGER.error().log("Error. No analyst recommendation found for {}.", symbol);
            return createPlaceholderAnalystRecommendation(symbol);
        } catch (JsonProcessingException e) {
            LOGGER.error().log("JSON processing exception deserializing analyst recommendation for {}", symbol);
            return createPlaceholderAnalystRecommendation(symbol);
        }
    }

    private boolean doesAnalystRecommendationExist(AnalystRecommendation[] analystRecommendation) {
        return analystRecommendation != null && analystRecommendation.length != 0;
    }

    private AnalystRecommendation createPlaceholderAnalystRecommendation(String symbol) {
        AnalystRecommendation placeHolderAnalystRecommendation = new AnalystRecommendation();
        placeHolderAnalystRecommendation.setSymbol(symbol);
        placeHolderAnalystRecommendation.setMarketConsensus(0F);
        placeHolderAnalystRecommendation.setMarketConsensusTargetPrice(new BigDecimal("0"));
        return placeHolderAnalystRecommendation;
    }
}
