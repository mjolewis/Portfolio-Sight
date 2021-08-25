package edu.bu.cs673.stockportfolio.marketdata.service.schedulingconfig;

import edu.bu.cs673.stockportfolio.marketdata.service.MarketDataServiceImpl;
import edu.bu.cs673.stockportfolio.marketdata.service.quote.QuoteService;
import org.fissore.slf4j.FluentLogger;
import org.fissore.slf4j.FluentLoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class LatestPriceScheduler {

    private static final FluentLogger LOGGER = FluentLoggerFactory.getLogger(LatestPriceScheduler.class);
    private final MarketDataServiceImpl marketDataServiceImpl;
    private final QuoteService quoteService;

    public LatestPriceScheduler(MarketDataServiceImpl marketDataServiceImpl,
                                QuoteService quoteService) {
        this.marketDataServiceImpl = marketDataServiceImpl;
        this.quoteService = quoteService;
    }

    /**
     * Configures the schedule for getting updated market quotes from IEX Cloud. The task uses cron scheduling to run
     * every minute from 9:00 AM to 4:00 PM Eastern time every day-of-week from Monday through Friday.
     */
    @Scheduled(cron = "0 */1 9-15,16 * * MON-FRI", zone = "America/New_York")
    public void startSchedule() {
        boolean isMarketOpen = isUSMarketOpen();
        if (isMarketOpen) {
            LOGGER.info().log("US stock market is open. Get latest prices from IEX Cloud");
            quoteService.getLatestPrices();
        } else {
            LOGGER.info().log("US stock market is closed. Potential holiday - Closed during normal trading hours");
        }
    }

    public boolean isUSMarketOpen() {
        return marketDataServiceImpl.isUSMarketOpen();
    }
}
