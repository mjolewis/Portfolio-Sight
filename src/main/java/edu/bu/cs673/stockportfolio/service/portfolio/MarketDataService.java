package edu.bu.cs673.stockportfolio.service.portfolio;

import edu.bu.cs673.stockportfolio.domain.investment.quote.Quote;
import edu.bu.cs673.stockportfolio.domain.investment.sector.Company;

import java.util.List;
import java.util.Set;

/**********************************************************************************************************************
 * The MarketDataService defines a template method for making API requests to IEX Cloud endpoints. The template method
 * controls the order or execution, but allows subclasses to override which endpoints are targeted.
 *********************************************************************************************************************/
public interface MarketDataService {

    boolean isUSMarketOpen();

    List<Quote> doGetQuotes(Set<String> symbols);

    List<Company> doGetCompanies(Set<String> symbols);
}
