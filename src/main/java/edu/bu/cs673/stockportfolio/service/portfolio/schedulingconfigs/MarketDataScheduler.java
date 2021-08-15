package edu.bu.cs673.stockportfolio.service.portfolio.schedulingconfigs;

import edu.bu.cs673.stockportfolio.service.portfolio.MarketDataServiceImpl;
import edu.bu.cs673.stockportfolio.service.portfolio.QuoteService;
import org.fissore.slf4j.FluentLogger;
import org.fissore.slf4j.FluentLoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class MarketDataScheduler {

    private static final FluentLogger LOGGER = FluentLoggerFactory.getLogger(MarketDataScheduler.class);
    private final MarketDataServiceImpl marketDataServiceImpl;
    private final QuoteService quoteService;

    public MarketDataScheduler(MarketDataServiceImpl marketDataServiceImpl,
                               QuoteService quoteService) {
        this.marketDataServiceImpl = marketDataServiceImpl;
        this.quoteService = quoteService;
    }

    /**
     * Configures the schedule for getting updated market quotes from IEX Cloud. The task uses cron scheduling to run
     * every minute from 4:00 AM to 5:00 PM Easter time every day-of-week from Monday through Friday. This schedule
     * captures pre-market trading hours through the end of the normal trading day.
     */
    @Scheduled(cron = "0 1 4-16 * * MON-FRI", zone = "GMT -5")
    public void startSchedule() {
        LOGGER.info().log("OPENING BELL: 9:30 AM eastern time. Start fetching real-time price updates");

        boolean isMarketOpen = isUSMarketOpen();
        if (isMarketOpen) {
            quoteService.getLatestPrices();
        } else {
            LOGGER.info().log("US stock market is closed. Potential holiday - Closed during normal trading hours");
        }
    }

    public boolean isUSMarketOpen() {
        return marketDataServiceImpl.isUSMarketOpen();
    }
}
