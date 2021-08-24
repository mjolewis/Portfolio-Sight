package edu.bu.cs673.stockportfolio.service.portfolio;

import edu.bu.cs673.stockportfolio.domain.investment.analysts.AnalystRecommendation;
import edu.bu.cs673.stockportfolio.domain.investment.quote.QuoteRoot;
import edu.bu.cs673.stockportfolio.domain.investment.sector.CompanyRoot;

import java.util.List;
import java.util.Set;

/**********************************************************************************************************************
 * The MarketDataService defines a template method for making API requests to IEX Cloud endpoints. The template method
 * controls the order or execution, but allows subclasses to override which endpoints are targeted.
 *********************************************************************************************************************/
public interface MarketDataService {

    boolean isUSMarketOpen();

    QuoteRoot doGetQuotes(Set<String> symbols);

    CompanyRoot doGetCompanies(Set<String> symbols);

    List<AnalystRecommendation> doGetAnalystRecommendations(Set<String> symbols);
}
