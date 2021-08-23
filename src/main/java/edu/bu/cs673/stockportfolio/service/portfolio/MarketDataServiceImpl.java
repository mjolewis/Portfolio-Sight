package edu.bu.cs673.stockportfolio.service.portfolio;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.bu.cs673.stockportfolio.domain.investment.analysts.AnalystRecommendation;
import edu.bu.cs673.stockportfolio.domain.investment.analysts.AnalystRecommendationRepository;
import edu.bu.cs673.stockportfolio.domain.investment.quote.Quote;
import edu.bu.cs673.stockportfolio.domain.investment.quote.QuoteRepository;
import edu.bu.cs673.stockportfolio.domain.investment.quote.QuoteRoot;
import edu.bu.cs673.stockportfolio.domain.investment.quote.StockQuote;
import edu.bu.cs673.stockportfolio.domain.investment.sector.Company;
import edu.bu.cs673.stockportfolio.domain.investment.sector.CompanyRoot;
import edu.bu.cs673.stockportfolio.domain.investment.sector.StockSector;
import edu.bu.cs673.stockportfolio.service.company.CompanyService;
import edu.bu.cs673.stockportfolio.service.utilities.IexCloudConfig;
import org.fissore.slf4j.FluentLogger;
import org.fissore.slf4j.FluentLoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

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
    private final QuoteRepository quoteRepository;
    private final CompanyService companyService;
    private final AnalystRecommendationRepository analystRecommendationRepository;

    public MarketDataServiceImpl(RestTemplate restTemplate,
                                 IexCloudConfig iexCloudConfig,
                                 QuoteRepository quoteRepository,
                                 CompanyService companyService,
                                 AnalystRecommendationRepository analystRecommendationRepository) {
        this.restTemplate = restTemplate;
        this.token = iexCloudConfig.getToken();
        this.quoteRepository = quoteRepository;
        this.companyService = companyService;
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
    public List<Quote> doGetQuotes(Set<String> symbols) {

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

        QuoteRoot quoteRoot = restTemplate.getForObject(
                BASE_URL
                        + VERSION
                        + endpointPath
                        + queryParams
                        + TOKEN_PARAM
                        + token
                , QuoteRoot.class);

        List<Quote> quotes = new ArrayList<>();
        if (quoteRoot != null) {
            Map<String, StockQuote> stocks = quoteRoot.getStocks();
            stocks.forEach((key, value) -> {
                Quote quoteToBeSaved = updateExistingQuoteOrGetNewQuote(value);
                quoteRepository.save(quoteToBeSaved);
                quotes.add(quoteToBeSaved);
            });
        }

        return quotes;
    }

    // Update the quote if it exists, otherwise return the new quote
    private Quote updateExistingQuoteOrGetNewQuote(StockQuote stockQuote) {
        Quote quote = stockQuote.getQuote();
        String symbol = quote.getSymbol();

        Quote existingQuote = quoteRepository.findQuoteBySymbol(symbol);
        if (existingQuote != null) {
            existingQuote.setMarketCap(quote.getMarketCap());
            existingQuote.setLatestPrice(quote.getLatestPrice());
            return existingQuote;
        }

        return quote;
    }

    @Override
    public List<Company> doGetCompanies(Set<String> symbols) {

        // Don't retrieve Company data that we already have so remove existing symbols from the request.
        Set<String> newSymbols = new HashSet<>();
        for (String symbol : symbols) {
            if (!companyService.contains(symbol)) {
                newSymbols.add(symbol);
            }
        }

        // If there are no new companies in this set, return early an empty list
        if (newSymbols.isEmpty()) return new ArrayList<>();

        String symbolFilter = String.join(",", newSymbols);
        String endpointPath = "stock/market/batch";
        String queryParams = String.format("?symbols=%s&types=company&filter=symbol,sector,companyName", symbolFilter);

        CompanyRoot companyRoot = restTemplate.getForObject(
                BASE_URL
                        + VERSION
                        + endpointPath
                        + queryParams
                        + TOKEN_PARAM
                        + token
                , CompanyRoot.class);

        List<Company> companies = new ArrayList<>();
        if (companyRoot != null) {
            Map<String, StockSector> companyData = companyRoot.getCompanies();
            companyData.forEach((key, value) -> companies.add(value.getCompany()));
        }

        return companies;
    }

    @Override
    public List<AnalystRecommendation> doGetAnalystRecommendations(Set<String> symbols) {

        List<AnalystRecommendation> analystRecommendations = new ArrayList<>();
        String queryParams = "?filter=symbol,marketConsensus,marketConsensusTargetPrice";
        symbols.forEach(symbol -> {
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
                LOGGER.error().log("JSON processing exception while deserializing analyst recommendations");
            }

            if (analystRecommendation != null) {
                AnalystRecommendation analystRecommendationToBeSaved =
                        updateExistingAnalystRecommendationOrGetNewAnalystRecommendation(analystRecommendation[0]);

                analystRecommendationRepository.save(analystRecommendationToBeSaved);
                analystRecommendations.add(analystRecommendationToBeSaved);
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

    // Finds the given ticker symbol if it exists in the specified service
    private boolean contains(String symbol) {
        List<AnalystRecommendation> analystRecommendations = analystRecommendationRepository.findAll();

        for (AnalystRecommendation analystRecommendation : analystRecommendations) {
            if (analystRecommendation.getSymbol().equals(symbol)) {
                return true;
            }
        }

        return false;
    }
}
