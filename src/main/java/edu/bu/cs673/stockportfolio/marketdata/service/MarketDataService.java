package edu.bu.cs673.stockportfolio.marketdata.service;

import edu.bu.cs673.stockportfolio.marketdata.model.consensus.persistence.AnalystRecommendation;
import edu.bu.cs673.stockportfolio.marketdata.model.quote.persistence.QuoteRoot;
import edu.bu.cs673.stockportfolio.marketdata.model.company.persistence.CompanyRoot;

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
