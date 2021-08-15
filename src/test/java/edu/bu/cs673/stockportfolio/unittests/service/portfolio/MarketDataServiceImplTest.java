package edu.bu.cs673.stockportfolio.unittests.service.portfolio;

import static org.mockito.Mockito.when;

import edu.bu.cs673.stockportfolio.domain.investment.quote.Quote;
import edu.bu.cs673.stockportfolio.domain.investment.quote.QuoteRepository;
import edu.bu.cs673.stockportfolio.domain.investment.quote.QuoteRoot;
import edu.bu.cs673.stockportfolio.domain.investment.quote.StockQuote;
import edu.bu.cs673.stockportfolio.domain.investment.sector.Company;
import edu.bu.cs673.stockportfolio.domain.investment.sector.CompanyRoot;
import edu.bu.cs673.stockportfolio.domain.investment.sector.StockSector;
import edu.bu.cs673.stockportfolio.service.company.CompanyService;
import edu.bu.cs673.stockportfolio.service.portfolio.MarketDataServiceImpl;
import edu.bu.cs673.stockportfolio.service.utilities.IexCloudConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public void getQuotesFromMarketDataApiShouldReturnValidQuotesAndGetConvertedIntoEntities() {
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

        StockQuote stockQuote = new StockQuote(quote);
        QuoteRoot quoteRoot = new QuoteRoot();
        quoteRoot.addStock("Goldman Sachs, Inc.", stockQuote);

        when(restTemplate.getForObject(BASE_URL + VERSION + endpointPath
                + queryParams + TOKEN + apiKey, QuoteRoot.class)).thenReturn(quoteRoot);

        when(quoteRepository.save(quote)).thenReturn(quote);

        List<Quote> result = marketDataService.doGetQuotes(symbols);

        Assertions.assertEquals(quote, result.get(0));
    }

    @Test
    public void getCompaniesFromMarketDataApiShouldReturnValidListOfCompaniesConvertedIntoEntities() {
        String symbolFilter = String.join(",", symbols);
        String endpointPath = "stock/market/batch";
        String queryParams = String.format("?symbols=%s&types=company&filter=symbol,sector,companyName", symbolFilter);

        List<Quote> quotes = List.of(quote);
        Company company =
                new Company("GS", "Financial Services", "Goldman Sachs, Inc.", quotes);
        StockSector companySector = new StockSector(company);
        CompanyRoot companyRoot = new CompanyRoot();
        companyRoot.addCompany("GS", companySector);

        when(restTemplate.getForObject(
                BASE_URL + VERSION + endpointPath + queryParams + TOKEN + apiKey, CompanyRoot.class))
                .thenReturn(companyRoot);

        List<Company> result = marketDataService.doGetCompanies(symbols);

        Assertions.assertEquals(company, result.get(0));
    }
}
