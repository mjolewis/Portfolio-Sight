package edu.bu.cs673.stockportfolio.unittests.service.portfolio;

import static org.mockito.Mockito.when;

import edu.bu.cs673.stockportfolio.marketdata.model.quote.persistence.Quote;
import edu.bu.cs673.stockportfolio.marketdata.model.quote.persistence.QuoteRepository;
import edu.bu.cs673.stockportfolio.marketdata.model.quote.persistence.QuoteRoot;
import edu.bu.cs673.stockportfolio.marketdata.model.quote.persistence.QuoteContainer;
import edu.bu.cs673.stockportfolio.marketdata.model.company.persistence.Company;
import edu.bu.cs673.stockportfolio.marketdata.model.company.persistence.CompanyRoot;
import edu.bu.cs673.stockportfolio.marketdata.model.company.persistence.CompanyContainer;
import edu.bu.cs673.stockportfolio.marketdata.service.company.CompanyService;
import edu.bu.cs673.stockportfolio.marketdata.service.MarketDataServiceImpl;
import edu.bu.cs673.stockportfolio.marketdata.service.quote.QuoteService;
import edu.bu.cs673.stockportfolio.marketdata.config.IexCloudConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

/*
 * https://rieckpil.de/difference-between-mock-and-mockbean-spring-boot-applications/
 */
@ExtendWith(MockitoExtension.class)
public class MarketDataServiceImplTest {

    private final String BASE_URL = "https://cloud.iexapis.com/";
    private final String VERSION = "stable/";
    private final String TOKEN = "&token=";

    @InjectMocks
    private MarketDataServiceImpl marketDataService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private QuoteService quoteService;

    @Mock
    private CompanyService companyService;

    @Mock
    private IexCloudConfig iexCloudConfig;

    private Set<String> symbols;

    private String apiKey;

    private Quote quote;

    @BeforeEach
    public void setup() {
        symbols = new HashSet<>(Set.of("GS"));

        apiKey = iexCloudConfig.getToken();

        quote = new Quote();
        quote.setId(1L);
        quote.setCompanyName("Goldman Sachs, Inc.");
        quote.setSymbol("GS");
        quote.setLatestPrice(new BigDecimal("345.53"));
        quote.setMarketCap(127370100000L);
    }

    @Test
    public void getQuotesFromMarketDataApiShouldReturnValidQuote() {
        // Convert the Set of Strings to a String for batch IEX request
        String symbolFilter = String.join(",", symbols);
        String endpointPath = "stock/market/batch";
        String queryParams = String.format(
                "?symbols=%s&types=quote&filter=" +
                        "companyName," +
                        "symbol," +
                        "latestPrice," +
                        "marketCap",
                symbolFilter);

        QuoteContainer quoteContainer = new QuoteContainer(quote);
        QuoteRoot quoteRoot = new QuoteRoot();
        quoteRoot.addQuoteContainer("Goldman Sachs, Inc.", quoteContainer);

        when(restTemplate.getForObject(BASE_URL + VERSION + endpointPath
                + queryParams + TOKEN + apiKey, QuoteRoot.class)).thenReturn(quoteRoot);

        QuoteRoot quoteRootResponse = marketDataService.doGetQuotes(symbols);

        Map<String, QuoteContainer> stocks = quoteRootResponse.getQuoteContainer();

        Collection<QuoteContainer> quoteContainers = stocks.values();
        quoteContainers.forEach(value -> {
            Assertions.assertEquals(quote, value.getQuote());
        });
    }

    @Test
    public void getCompaniesFromMarketDataApiShouldReturnValidCompany() {
        String symbolFilter = String.join(",", symbols);
        String endpointPath = "stock/market/batch";
        String queryParams = String.format("?symbols=%s&types=company&filter=symbol,sector,companyName", symbolFilter);

        List<Quote> quotes = List.of(quote);
        Company company =
                new Company("GS", "Financial Services", "Goldman Sachs, Inc.", quotes);
        CompanyContainer companyContainer = new CompanyContainer(company);
        CompanyRoot companyRoot = new CompanyRoot();
        companyRoot.addCompanyContainer("GS", companyContainer);

        when(restTemplate.getForObject(
                BASE_URL + VERSION + endpointPath + queryParams + TOKEN + apiKey, CompanyRoot.class))
                .thenReturn(companyRoot);

        CompanyRoot response = marketDataService.doGetCompanies(symbols);

        Map<String, CompanyContainer> sectors = response.getCompanyContainer();

        Collection<CompanyContainer> companies = sectors.values();
        companies.forEach(value -> Assertions.assertEquals(company, value.getCompany()));
    }
}
