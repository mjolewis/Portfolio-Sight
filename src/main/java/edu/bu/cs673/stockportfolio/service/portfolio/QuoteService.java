package edu.bu.cs673.stockportfolio.service.portfolio;

import edu.bu.cs673.stockportfolio.domain.investment.quote.Quote;
import edu.bu.cs673.stockportfolio.domain.investment.quote.QuoteRepository;
import edu.bu.cs673.stockportfolio.domain.investment.quote.QuoteRoot;
import edu.bu.cs673.stockportfolio.domain.investment.quote.StockQuote;
import org.fissore.slf4j.FluentLogger;
import org.fissore.slf4j.FluentLoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class QuoteService {

    private static final FluentLogger LOGGER = FluentLoggerFactory.getLogger(QuoteService.class);
    private final QuoteRepository quoteRepository;
    private final MarketDataServiceImpl marketDataServiceImpl;

    public QuoteService(QuoteRepository quoteRepository, MarketDataServiceImpl marketDataServiceImpl) {
        this.quoteRepository = quoteRepository;
        this.marketDataServiceImpl = marketDataServiceImpl;
    }

    @Transactional
    public List<Quote> processQuoteRootRestTemplate(QuoteRoot quoteRoot) {
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

    /**
     * Gets the latestPrice from IEXCloud for a basket of securities. This task is scheduled by the
     * QuoteServiceScheduler.
     */
    @Transactional
    public void getLatestPrices() {
        LOGGER.info().log("Getting latest prices from IEX Cloud");
        List<Quote> existingQuotes = quoteRepository.findAll();

        // Package the existing quotes into a set for the marketDataServiceImpl
        Set<String> allSymbols = new HashSet<>();
        existingQuotes.forEach(quoteToBeUpdated -> {
            allSymbols.add(String.join(",", quoteToBeUpdated.getSymbol()));
        });

        // GET new quotes from IEX Cloud
        if (allSymbols.size() != 0) {
            QuoteRoot quoteRoot = marketDataServiceImpl.doGetQuotes(allSymbols);
            List<Quote> quotes = processQuoteRootRestTemplate(quoteRoot);

            quotes.forEach(quote -> {
                existingQuotes.forEach(existingQuote -> {
                    if (existingQuote.getSymbol().equals(quote.getSymbol())) {

                        // Pull the existing quote into the persistence context
                        Optional<Quote> optionalQuote = quoteRepository.findById(existingQuote.getId());

                        if (optionalQuote.isPresent()) {
                            Quote quoteToBeUpdated = optionalQuote.get();
                            quoteToBeUpdated.setLatestPrice(quote.getLatestPrice());
                        }
                    }
                });
            });
        }
    }
}
